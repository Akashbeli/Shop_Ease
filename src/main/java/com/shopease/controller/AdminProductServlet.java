package com.shopease.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shopease.dao.ProductDao;
import com.shopease.model.Product;

/** Admin: list, add, edit and delete products. The AuthFilter keeps everyone else out. */
@WebServlet("/admin/products")
public class AdminProductServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private final ProductDao productDao = new ProductDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = str(req, "action");

        if ("new".equals(action)) {
            showForm(req, resp, new Product(), null);
            return;
        }
        if ("edit".equals(action)) {
            Product product = productDao.findById(intParam(req, "id", 0));
            if (product == null) {
                flash(req, "error", "That product no longer exists.");
                redirect(req, resp, "/admin/products");
                return;
            }
            showForm(req, resp, product, null);
            return;
        }
        req.setAttribute("products", productDao.findAll());
        render(req, resp, "admin/products", "Manage products");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("delete".equals(str(req, "action"))) {
            productDao.delete(intParam(req, "id", 0));
            flash(req, "success", "Product deleted.");
            redirect(req, resp, "/admin/products");
            return;
        }

        List<String> errors = new ArrayList<>();
        Product p = new Product();
        p.setId(intParam(req, "id", 0));
        p.setName(str(req, "name"));
        p.setDescription(str(req, "description"));
        p.setCategory(str(req, "category"));
        p.setIcon(str(req, "icon"));
        p.setImageUrl(str(req, "imageUrl"));
        p.setStock(intParam(req, "stock", -1));

        try {
            p.setPrice(new BigDecimal(str(req, "price")));
            if (p.getPrice().signum() <= 0) {
                errors.add("Price must be more than zero.");
            }
        } catch (NumberFormatException e) {
            errors.add("Price must be a number, for example 499 or 499.50.");
        }
        try {
            p.setRating(Double.parseDouble(str(req, "rating")));
            if (p.getRating() < 0 || p.getRating() > 5) {
                errors.add("Rating must be between 0 and 5.");
            }
        } catch (NumberFormatException e) {
            errors.add("Rating must be a number between 0 and 5.");
        }
        if (p.getName().isEmpty() || p.getName().length() > 120) {
            errors.add("Name is required (up to 120 characters).");
        }
        if (p.getCategory().isEmpty() || p.getCategory().length() > 50) {
            errors.add("Category is required (up to 50 characters).");
        }
        if (p.getStock() < 0) {
            errors.add("Stock must be 0 or more.");
        }
        if (p.getImageUrl().length() > 255) {
            errors.add("Image link is too long.");
        }
        if (!Product.ICON_KEYS.contains(p.getIcon())) {
            p.setIcon("bag");
        }

        if (!errors.isEmpty()) {
            showForm(req, resp, p, errors);
            return;
        }
        if (p.getId() == 0) {
            productDao.insert(p);
        } else {
            productDao.update(p);
        }
        flash(req, "success", "Saved \"" + p.getName() + "\".");
        redirect(req, resp, "/admin/products");
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Product product, List<String> errors)
            throws ServletException, IOException {
        req.setAttribute("product", product);
        req.setAttribute("errors", errors);
        req.setAttribute("icons", Product.ICON_KEYS);
        req.setAttribute("categories", productDao.findCategories());
        render(req, resp, "admin/product-form", product.getId() == 0 ? "Add product" : "Edit product");
    }
}
