package com.shopease.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shopease.exception.ShopException;
import com.shopease.model.Cart;
import com.shopease.model.Product;
import com.shopease.service.CartService;

/** GET shows the cart. POST changes it (add, update, remove, clear). */
@WebServlet("/cart")
public class CartServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cart cart = currentCart(req);
        if (cartService.refresh(cart)) {
            flash(req, "info", "We updated your cart to match the latest prices and stock.");
        }
        req.setAttribute("cart", cart);
        render(req, resp, "cart", "Your cart");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cart cart = currentCart(req);
        String action = str(req, "action");
        int productId = intParam(req, "productId", 0);
        String type = "success";
        String message = "";

        try {
            switch (action) {
                case "add": {
                    Product added = cartService.add(cart, productId, Math.max(1, intParam(req, "quantity", 1)));
                    message = "Added \"" + added.getName() + "\" to your cart.";
                    break;
                }
                case "update":
                    cartService.update(cart, productId, intParam(req, "quantity", 1));
                    message = "Cart updated.";
                    break;
                case "remove":
                    cart.remove(productId);
                    message = "Item removed.";
                    break;
                case "clear":
                    cart.clear();
                    message = "Your cart is empty now.";
                    break;
                default:
                    type = "error";
                    message = "That cart action is not supported.";
            }
        } catch (ShopException e) {
            type = "error";
            message = e.getMessage();
        }

        // The "Add" buttons use fetch() and only need a little JSON back.
        if (isAjax(req)) {
            writeJson(resp, !"error".equals(type), cart.getItemCount(), message);
            return;
        }
        flash(req, type, message);
        if ("add".equals(action)) {
            redirectBack(req, resp, "/cart");
        } else {
            redirect(req, resp, "/cart");
        }
    }
}
