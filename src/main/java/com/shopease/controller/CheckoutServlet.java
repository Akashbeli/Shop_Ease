package com.shopease.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shopease.exception.OutOfStockException;
import com.shopease.exception.ShopException;
import com.shopease.model.Cart;
import com.shopease.model.Order;
import com.shopease.payment.PaymentFactory;
import com.shopease.service.CartService;
import com.shopease.service.OrderService;

/** GET shows the delivery and payment form. POST validates it and places the order. */
@WebServlet("/checkout")
public class CheckoutServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private static final Pattern PHONE = Pattern.compile("^[0-9]{10}$");
    private static final Pattern PIN = Pattern.compile("^[0-9]{6}$");

    private final CartService cartService = new CartService();
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cart cart = currentCart(req);
        cartService.refresh(cart);
        if (cart.getItemCount() == 0) {
            sendToEmptyCart(req, resp);
            return;
        }
        show(req, resp, cart, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cart cart = currentCart(req);
        cartService.refresh(cart);
        if (cart.getItemCount() == 0) {
            sendToEmptyCart(req, resp);
            return;
        }

        String name = str(req, "name");
        String phone = str(req, "phone");
        String address = str(req, "address");
        String city = str(req, "city");
        String pin = str(req, "pin");

        List<String> errors = new ArrayList<>();
        if (name.length() < 2 || name.length() > 80) {
            errors.add("Enter the name of the person receiving the order.");
        }
        if (!PHONE.matcher(phone).matches()) {
            errors.add("Enter a 10-digit mobile number.");
        }
        if (address.length() < 5 || address.length() > 150) {
            errors.add("Enter the delivery address (house, street, area).");
        }
        if (city.isEmpty() || city.length() > 50) {
            errors.add("Enter the city.");
        }
        if (!PIN.matcher(pin).matches()) {
            errors.add("Enter a 6-digit PIN code.");
        }

        if (errors.isEmpty()) {
            try {
                Order order = orderService.placeOrder(currentUser(req), cart, name, phone,
                        address + ", " + city + " - " + pin, str(req, "payment"));
                cart.clear();
                redirect(req, resp, "/order?id=" + order.getId() + "&placed=1");
                return;
            } catch (OutOfStockException e) {
                flash(req, "error", e.getMessage());
                redirect(req, resp, "/cart");
                return;
            } catch (ShopException e) {
                errors.add(e.getMessage());
            }
        }
        show(req, resp, cart, errors);
    }

    private void sendToEmptyCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        flash(req, "info", "Your cart is empty. Add something first.");
        redirect(req, resp, "/cart");
    }

    private void show(HttpServletRequest req, HttpServletResponse resp, Cart cart, List<String> errors)
            throws ServletException, IOException {
        req.setAttribute("cart", cart);
        req.setAttribute("paymentMethods", PaymentFactory.all());
        req.setAttribute("errors", errors);
        render(req, resp, "checkout", "Checkout");
    }
}
