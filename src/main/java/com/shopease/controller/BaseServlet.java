package com.shopease.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.shopease.model.Cart;
import com.shopease.model.User;

/** Parent of every controller (Inheritance): small helpers they all need. */
public abstract class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected static final String USER = "user";
    protected static final String CART = "cart";

    protected User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute(USER);
    }

    protected Cart currentCart(HttpServletRequest req) {
        HttpSession session = req.getSession();
        Cart cart = (Cart) session.getAttribute(CART);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(CART, cart);
        }
        return cart;
    }

    /** Logs the user in. A new session id is issued (stops session fixation); the cart is kept. */
    protected void loginAs(HttpServletRequest req, User user) {
        HttpSession session = req.getSession();
        req.changeSessionId();
        user.setPasswordHash(null);
        session.setAttribute(USER, user);
    }

    /** A one-time message shown on the next page that is rendered. type: success, error or info. */
    protected void flash(HttpServletRequest req, String type, String message) {
        HttpSession session = req.getSession();
        session.setAttribute("flashType", type);
        session.setAttribute("flashMsg", message);
    }

    /** Forwards to /WEB-INF/views/{view}.jsp (those files cannot be opened directly from a browser). */
    protected void render(HttpServletRequest req, HttpServletResponse resp, String view, String title)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("flashMsg") != null) {
            req.setAttribute("flashMsg", session.getAttribute("flashMsg"));
            req.setAttribute("flashType", session.getAttribute("flashType"));
            session.removeAttribute("flashMsg");
            session.removeAttribute("flashType");
        }
        req.setAttribute("ctx", req.getContextPath());
        req.setAttribute("pageTitle", title);
        req.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    /** Sends the shopper back to the page they came from (same site only). */
    protected void redirectBack(HttpServletRequest req, HttpServletResponse resp, String fallback)
            throws IOException {
        String referer = req.getHeader("Referer");
        if (referer != null && referer.contains("://" + req.getServerName())) {
            resp.sendRedirect(referer);
        } else {
            redirect(req, resp, fallback);
        }
    }

    /** Trimmed request parameter, never null. */
    protected String str(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value == null ? "" : value.trim();
    }

    protected int intParam(HttpServletRequest req, String name, int fallback) {
        try {
            return Integer.parseInt(str(req, name));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /** The ?next= page to return to after login. Only same-site paths are accepted. */
    protected String safeNext(HttpServletRequest req) {
        String next = str(req, "next");
        boolean ok = next.startsWith("/") && !next.startsWith("//") && !next.contains("\\") && !next.contains(":");
        return ok ? next : "";
    }

    protected boolean isAjax(HttpServletRequest req) {
        return "fetch".equals(req.getHeader("X-Requested-With"));
    }

    protected void writeJson(HttpServletResponse resp, boolean ok, int count, String message) throws IOException {
        String safe = message.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("{\"ok\":" + ok + ",\"count\":" + count + ",\"message\":\"" + safe + "\"}");
    }
}
