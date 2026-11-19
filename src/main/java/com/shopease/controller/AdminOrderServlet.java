package com.shopease.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shopease.model.OrderStatus;
import com.shopease.service.OrderService;

/** Admin: see every order and move it along (placed, paid, shipped, delivered, cancelled). */
@WebServlet("/admin/orders")
public class AdminOrderServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("orders", orderService.findAll());
        req.setAttribute("statuses", OrderStatus.values());
        render(req, resp, "admin/orders", "Manage orders");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = intParam(req, "id", 0);
        try {
            OrderStatus status = OrderStatus.valueOf(str(req, "status"));
            orderService.updateStatus(id, status);
            flash(req, "success", "Order #" + id + " is now " + status.getLabel().toLowerCase() + ".");
        } catch (IllegalArgumentException e) {
            flash(req, "error", "That status is not valid.");
        }
        redirect(req, resp, "/admin/orders");
    }
}
