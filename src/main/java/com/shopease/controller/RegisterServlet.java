package com.shopease.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shopease.dao.UserDao;
import com.shopease.model.User;
import com.shopease.util.PasswordUtil;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (currentUser(req) != null) {
            redirect(req, resp, "/shop");
            return;
        }
        req.setAttribute("next", safeNext(req));
        render(req, resp, "register", "Create account");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = str(req, "name");
        String email = str(req, "email").toLowerCase();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String confirm = req.getParameter("confirm") == null ? "" : req.getParameter("confirm");

        List<String> errors = new ArrayList<>();
        if (name.length() < 2 || name.length() > 80) {
            errors.add("Enter your full name.");
        }
        if (email.length() > 120 || !EMAIL.matcher(email).matches()) {
            errors.add("Enter a valid email address.");
        }
        if (password.length() < 6) {
            errors.add("Use at least 6 characters for your password.");
        }
        if (!password.equals(confirm)) {
            errors.add("The two passwords don't match.");
        }
        if (errors.isEmpty() && userDao.emailExists(email)) {
            errors.add("An account with this email already exists. Log in instead.");
        }
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("next", safeNext(req));
            render(req, resp, "register", "Create account");
            return;
        }

        User user = new User(name, email, PasswordUtil.hash(password));
        user.setId(userDao.insert(user));
        loginAs(req, user);
        flash(req, "success", "Welcome to ShopEase, " + user.getFirstName() + "!");
        String next = safeNext(req);
        redirect(req, resp, next.isEmpty() ? "/shop" : next);
    }
}
