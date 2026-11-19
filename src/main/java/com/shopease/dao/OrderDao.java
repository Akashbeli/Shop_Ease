package com.shopease.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.shopease.exception.DataAccessException;
import com.shopease.exception.OutOfStockException;
import com.shopease.model.Cart;
import com.shopease.model.CartItem;
import com.shopease.model.Order;
import com.shopease.model.OrderItem;
import com.shopease.model.OrderStatus;
import com.shopease.model.Product;
import com.shopease.util.DBConnection;

public class OrderDao implements Dao<Order> {

    private static final String COLUMNS =
            "id, user_id, total, status, payment_method, ship_name, ship_phone, ship_address, created_at";

    @Override
    public Order findById(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT " + COLUMNS + " FROM orders WHERE id = ?")) {
            ps.setInt(1, id);
            Order order = null;
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    order = map(rs);
                }
            }
            if (order != null) {
                order.setItems(loadItems(con, order.getId()));
            }
            return order;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load order " + id, e);
        }
    }

    /** All orders with the customer's name (admin screen). Lines are not loaded, only a unit count. */
    @Override
    public List<Order> findAll() {
        String sql = "SELECT o.id, o.user_id, o.total, o.status, o.payment_method, o.ship_name, o.ship_phone, "
                + "o.ship_address, o.created_at, u.name AS customer_name, "
                + "(SELECT COALESCE(SUM(oi.quantity), 0) FROM order_items oi WHERE oi.order_id = o.id) AS unit_count "
                + "FROM orders o JOIN users u ON u.id = o.user_id ORDER BY o.created_at DESC, o.id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Order> list = new ArrayList<>();
            while (rs.next()) {
                Order order = map(rs);
                order.setCustomerName(rs.getString("customer_name"));
                order.setItemCountFromDb(rs.getInt("unit_count"));
                list.add(order);
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load orders", e);
        }
    }

    public List<Order> findByUser(int userId) {
        String sql = "SELECT " + COLUMNS + " FROM orders WHERE user_id = ? ORDER BY created_at DESC, id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            List<Order> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            for (Order order : list) {
                order.setItems(loadItems(con, order.getId()));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load your orders", e);
        }
    }

    public void updateStatus(int orderId, OrderStatus status) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE orders SET status = ? WHERE id = ?")) {
            ps.setString(1, status.name());
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update order " + orderId, e);
        }
    }

    /**
     * Saves the order in ONE transaction: take stock, insert the order, insert its lines.
     * If anything fails, everything is rolled back, so we never sell stock we do not have.
     *
     * @return the new order id
     */
    public int create(Order order, Cart cart) throws OutOfStockException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                reserveStock(con, cart);
                int orderId = insertOrder(con, order);
                insertItems(con, orderId, cart);
                con.commit();
                return orderId;
            } catch (OutOfStockException | SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not place the order", e);
        }
    }

    // ---------- helpers ----------

    private void reserveStock(Connection con, Cart cart) throws SQLException, OutOfStockException {
        String sql = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (CartItem item : cart.getItems()) {
                ps.setInt(1, item.getQuantity());
                ps.setInt(2, item.getProduct().getId());
                ps.setInt(3, item.getQuantity());
                if (ps.executeUpdate() == 0) {
                    throw new OutOfStockException(
                            "Sorry, \"" + item.getProduct().getName() + "\" just ran low on stock.");
                }
            }
        }
    }

    private int insertOrder(Connection con, Order order) throws SQLException {
        String sql = "INSERT INTO orders (user_id, total, status, payment_method, ship_name, ship_phone, "
                + "ship_address, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserId());
            ps.setBigDecimal(2, order.getTotal());
            ps.setString(3, order.getStatus().name());
            ps.setString(4, order.getPaymentMethod());
            ps.setString(5, order.getShipName());
            ps.setString(6, order.getShipPhone());
            ps.setString(7, order.getShipAddress());
            ps.setTimestamp(8, new Timestamp(order.getCreatedAt().getTime()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("No order id returned");
                }
                return keys.getInt(1);
            }
        }
    }

    private void insertItems(Connection con, int orderId, Cart cart) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, product_name, icon, unit_price, quantity) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (CartItem item : cart.getItems()) {
                Product p = item.getProduct();
                ps.setInt(1, orderId);
                ps.setInt(2, p.getId());
                ps.setString(3, p.getName());
                ps.setString(4, p.getIcon());
                ps.setBigDecimal(5, p.getPrice());
                ps.setInt(6, item.getQuantity());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<OrderItem> loadItems(Connection con, int orderId) throws SQLException {
        String sql = "SELECT product_id, product_name, icon, unit_price, quantity "
                + "FROM order_items WHERE order_id = ? ORDER BY id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                List<OrderItem> items = new ArrayList<>();
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setProductId(rs.getInt("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setIcon(rs.getString("icon"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setQuantity(rs.getInt("quantity"));
                    items.add(item);
                }
                return items;
            }
        }
    }

    private Order map(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setUserId(rs.getInt("user_id"));
        o.setTotal(rs.getBigDecimal("total"));
        o.setStatus(OrderStatus.valueOf(rs.getString("status")));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setShipName(rs.getString("ship_name"));
        o.setShipPhone(rs.getString("ship_phone"));
        o.setShipAddress(rs.getString("ship_address"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        return o;
    }
}
