package com.shopease.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shopease.model.Order;
import com.shopease.model.User;
import com.shopease.service.OrderService;

/** /orders lists the shopper's orders, /order?id=5 shows one of them. */
@WebServlet(urlPatterns = {"/orders", "/order"})
public class OrderServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);

        if ("/orders".equals(req.getServletPath())) {
            req.setAttribute("orders", orderService.findByUser(user.getId()));
            render(req, resp, "orders", "My orders");
            return;
        }

        Order order = orderService.findForUser(intParam(req, "id", 0), user.getId());
        if (order == null) {
            flash(req, "error", "We couldn't find that order.");
            redirect(req, resp, "/orders");
            return;
        }
        req.setAttribute("order", order);
        req.setAttribute("placed", "1".equals(str(req, "placed")));
        render(req, resp, "order", "Order #" + order.getId());
    }
}
