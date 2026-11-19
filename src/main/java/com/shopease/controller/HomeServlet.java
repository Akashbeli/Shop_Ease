package com.shopease.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shopease.dao.ProductDao;
import com.shopease.model.Product;

/** The shop front: product grid with search, category filter and sorting. */
@WebServlet("/shop")
public class HomeServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final ProductDao productDao = new ProductDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = str(req, "q");
        String category = str(req, "category");
        String sort = str(req, "sort");
        if (sort.isEmpty()) {
            sort = "featured";
        }

        List<Product> products = productDao.search(q, category, sort);
        req.setAttribute("products", products);
        req.setAttribute("resultCount", products.size());
        req.setAttribute("categories", productDao.findCategories());
        req.setAttribute("q", q);
        req.setAttribute("category", category);
        req.setAttribute("sort", sort);
        req.setAttribute("filtered", !q.isEmpty() || !category.isEmpty());
        render(req, resp, "products", category.isEmpty() ? "Shop" : category);
    }
}
