package com.shopease.filter;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.shopease.model.User;

/** Gatekeeper: checkout and orders need a login, /admin/* needs an admin. */
@WebFilter(urlPatterns = {"/checkout", "/orders", "/order", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (user == null) {
            String query = req.getQueryString();
            String next = req.getServletPath() + (query == null ? "" : "?" + query);
            req.getSession().setAttribute("flashType", "info");
            req.getSession().setAttribute("flashMsg", "Please log in to continue.");
            resp.sendRedirect(req.getContextPath() + "/login?next=" + URLEncoder.encode(next, "UTF-8"));
            return;
        }
        if (req.getServletPath().startsWith("/admin") && !user.isAdmin()) {
            session.setAttribute("flashType", "error");
            session.setAttribute("flashMsg", "That page is for store admins only.");
            resp.sendRedirect(req.getContextPath() + "/shop");
            return;
        }
        chain.doFilter(request, response);
    }
}
