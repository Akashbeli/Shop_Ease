package com.shopease.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shopease.dao.UserDao;
import com.shopease.model.User;
import com.shopease.util.PasswordUtil;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (currentUser(req) != null) {
            redirect(req, resp, "/shop");
            return;
        }
        req.setAttribute("next", safeNext(req));
        render(req, resp, "login", "Log in");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = str(req, "email").toLowerCase();
        String password = req.getParameter("password");

        User user = userDao.findByEmail(email);
        if (user == null || !PasswordUtil.verify(password, user.getPasswordHash())) {
            req.setAttribute("error", "That email and password don't match. Check them and try again.");
            req.setAttribute("next", safeNext(req));
            render(req, resp, "login", "Log in");
            return;
        }

        loginAs(req, user);
        flash(req, "success", "Welcome back, " + user.getFirstName() + "!");
        String next = safeNext(req);
        redirect(req, resp, next.isEmpty() ? "/shop" : next);
    }
}
