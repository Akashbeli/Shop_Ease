package com.shopease.service;

import com.shopease.dao.ProductDao;
import com.shopease.exception.OutOfStockException;
import com.shopease.exception.ShopException;
import com.shopease.model.Cart;
import com.shopease.model.CartItem;
import com.shopease.model.Product;

/** Cart rules that need the database (fresh prices and stock). */
public class CartService {

    private final ProductDao productDao = new ProductDao();

    /** @return the product that was added, so the page can name it */
    public Product add(Cart cart, int productId, int quantity) throws ShopException {
        Product product = productDao.findById(productId);
        if (product == null) {
            throw new ShopException("That product is no longer available.");
        }
        if (!product.isInStock()) {
            throw new OutOfStockException("\"" + product.getName() + "\" is sold out.");
        }
        cart.add(product, quantity);
        return product;
    }

    public void update(Cart cart, int productId, int quantity) throws ShopException {
        if (quantity <= 0) {
            cart.remove(productId);
            return;
        }
        Product product = productDao.findById(productId);
        if (product == null) {
            cart.remove(productId);
            throw new ShopException("That product is no longer available.");
        }
        cart.setQuantity(product, quantity);
    }

    /**
     * Re-reads price and stock from MySQL so the cart is never out of date.
     *
     * @return true if anything had to change
     */
    public boolean refresh(Cart cart) {
        boolean changed = false;
        for (CartItem item : cart.getItems()) {
            Product old = item.getProduct();
            Product fresh = productDao.findById(old.getId());
            if (fresh == null || !fresh.isInStock()) {
                cart.remove(old.getId());
                changed = true;
                continue;
            }
            if (fresh.getPrice().compareTo(old.getPrice()) != 0 || item.getQuantity() > fresh.getStock()) {
                changed = true;
            }
            item.setProduct(fresh);
            if (item.getQuantity() > fresh.getStock()) {
                item.setQuantity(fresh.getStock());
            }
        }
        return changed;
    }
}
