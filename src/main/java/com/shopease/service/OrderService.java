package com.shopease.service;

import java.util.Date;
import java.util.List;

import com.shopease.dao.OrderDao;
import com.shopease.exception.ShopException;
import com.shopease.model.Cart;
import com.shopease.model.Order;
import com.shopease.model.OrderStatus;
import com.shopease.model.User;
import com.shopease.payment.PaymentFactory;
import com.shopease.payment.PaymentMethod;

public class OrderService {

    private final OrderDao orderDao = new OrderDao();

    public Order placeOrder(User user, Cart cart, String name, String phone, String address, String paymentCode)
            throws ShopException {
        if (cart.getItemCount() == 0) {
            throw new ShopException("Your cart is empty.");
        }
        PaymentMethod payment = PaymentFactory.find(paymentCode);

        Order order = new Order();
        order.setUserId(user.getId());
        order.setTotal(cart.getTotal());
        order.setStatus(payment.getInitialStatus());
        order.setPaymentMethod(payment.getCode());
        order.setShipName(name);
        order.setShipPhone(phone);
        order.setShipAddress(address);
        order.setCreatedAt(new Date());

        order.setId(orderDao.create(order, cart));
        return order;
    }

    public List<Order> findByUser(int userId) {
        List<Order> orders = orderDao.findByUser(userId);
        for (Order order : orders) {
            decorate(order);
        }
        return orders;
    }

    /** Only returns the order if it belongs to this user. */
    public Order findForUser(int orderId, int userId) {
        Order order = orderDao.findById(orderId);
        if (order == null || order.getUserId() != userId) {
            return null;
        }
        return decorate(order);
    }

    public List<Order> findAll() {
        List<Order> orders = orderDao.findAll();
        for (Order order : orders) {
            decorate(order);
        }
        return orders;
    }

    public void updateStatus(int orderId, OrderStatus status) {
        orderDao.updateStatus(orderId, status);
    }

    private Order decorate(Order order) {
        try {
            order.setPaymentLabel(PaymentFactory.find(order.getPaymentMethod()).getLabel());
        } catch (ShopException e) {
            order.setPaymentLabel(order.getPaymentMethod());
        }
        return order;
    }
}
