package com.shopease.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shopease.dao.ProductDao;
import com.shopease.model.Product;

@WebServlet("/product")
public class ProductServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final ProductDao productDao = new ProductDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Product product = productDao.findById(intParam(req, "id", 0));
        if (product == null) {
            flash(req, "error", "We couldn't find that product.");
            redirect(req, resp, "/shop");
            return;
        }
        req.setAttribute("product", product);
        req.setAttribute("related", productDao.findRelated(product, 4));
        render(req, resp, "product", product.getName());
    }
}
