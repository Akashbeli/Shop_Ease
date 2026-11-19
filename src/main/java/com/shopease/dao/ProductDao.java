package com.shopease.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.shopease.exception.DataAccessException;
import com.shopease.model.Category;
import com.shopease.model.Product;
import com.shopease.util.DBConnection;

public class ProductDao implements Dao<Product> {

    private static final String COLUMNS = "id, name, description, category, price, stock, rating, icon, image_url";

    @Override
    public Product findById(int id) {
        String sql = "SELECT " + COLUMNS + " FROM products WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load product " + id, e);
        }
    }

    @Override
    public List<Product> findAll() {
        return query("SELECT " + COLUMNS + " FROM products ORDER BY id");
    }

    /** Search box + category filter + sort order, all optional. */
    public List<Product> search(String keyword, String category, String sort) {
        StringBuilder sql = new StringBuilder("SELECT " + COLUMNS + " FROM products WHERE 1 = 1");
        List<String> params = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (name LIKE ? OR description LIKE ? OR category LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (category != null && !category.isEmpty()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        sql.append(" ORDER BY ").append(orderBy(sort));
        return query(sql.toString(), params.toArray(new String[0]));
    }

    public List<Product> findRelated(Product product, int limit) {
        return query("SELECT " + COLUMNS + " FROM products WHERE category = ? AND id <> " + product.getId()
                + " ORDER BY rating DESC, id LIMIT " + limit, product.getCategory());
    }

    /** Categories with how many products each has, plus the icon of the best-rated product. */
    public List<Category> findCategories() {
        String sql = "SELECT p.category, COUNT(*) AS cnt, "
                + "(SELECT p2.icon FROM products p2 WHERE p2.category = p.category "
                + "ORDER BY p2.rating DESC, p2.id LIMIT 1) AS icon "
                + "FROM products p GROUP BY p.category ORDER BY p.category";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Category> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Category(rs.getString("category"), rs.getInt("cnt"), rs.getString("icon")));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load categories", e);
        }
    }

    public int insert(Product p) {
        String sql = "INSERT INTO products (name, description, category, price, stock, rating, icon, image_url) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, p);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not add product", e);
        }
    }

    public void update(Product p) {
        String sql = "UPDATE products SET name = ?, description = ?, category = ?, price = ?, stock = ?, "
                + "rating = ?, icon = ?, image_url = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, p);
            ps.setInt(9, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update product " + p.getId(), e);
        }
    }

    public boolean delete(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM products WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete product " + id, e);
        }
    }

    // ---------- helpers ----------

    private List<Product> query(String sql, String... params) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Product> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load products", e);
        }
    }

    /** Only whitelisted text ever reaches ORDER BY, so user input cannot inject SQL here. */
    private String orderBy(String sort) {
        if (sort == null) {
            return "rating DESC, id";
        }
        switch (sort) {
            case "price_asc":
                return "price ASC, id";
            case "price_desc":
                return "price DESC, id";
            case "newest":
                return "id DESC";
            default:
                return "rating DESC, id";
        }
    }

    private void bind(PreparedStatement ps, Product p) throws SQLException {
        ps.setString(1, p.getName());
        ps.setString(2, p.getDescription());
        ps.setString(3, p.getCategory());
        ps.setBigDecimal(4, p.getPrice());
        ps.setInt(5, p.getStock());
        ps.setDouble(6, p.getRating());
        ps.setString(7, p.getIcon());
        ps.setString(8, p.getImageUrl().isEmpty() ? null : p.getImageUrl());
    }

    private Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setCategory(rs.getString("category"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStock(rs.getInt("stock"));
        p.setRating(rs.getDouble("rating"));
        p.setIcon(rs.getString("icon"));
        p.setImageUrl(rs.getString("image_url"));
        return p;
    }
}
