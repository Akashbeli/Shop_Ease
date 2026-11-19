package com.shopease.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.shopease.exception.OutOfStockException;
import com.shopease.util.Money;

/** The shopper's basket. Lives in the HttpSession, so every method is synchronized. */
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final BigDecimal FREE_SHIPPING_MIN = new BigDecimal("999");
    public static final BigDecimal SHIPPING_FEE = new BigDecimal("49");

    /** productId -> line item (LinkedHashMap keeps the order items were added). */
    private final Map<Integer, CartItem> items = new LinkedHashMap<>();

    public synchronized void add(Product product, int quantity) throws OutOfStockException {
        CartItem existing = items.get(product.getId());
        int wanted = (existing == null ? 0 : existing.getQuantity()) + quantity;
        checkStock(product, wanted);
        put(product, wanted);
    }

    public synchronized void setQuantity(Product product, int quantity) throws OutOfStockException {
        if (quantity <= 0) {
            items.remove(product.getId());
            return;
        }
        checkStock(product, quantity);
        put(product, quantity);
    }

    public synchronized void remove(int productId) {
        items.remove(productId);
    }

    public synchronized void clear() {
        items.clear();
    }

    private void put(Product product, int quantity) {
        CartItem existing = items.get(product.getId());
        if (existing == null) {
            items.put(product.getId(), new CartItem(product, quantity));
        } else {
            existing.setProduct(product);
            existing.setQuantity(quantity);
        }
    }

    private void checkStock(Product product, int wanted) throws OutOfStockException {
        if (wanted > product.getStock()) {
            throw new OutOfStockException(product.getStock() <= 0
                    ? "\"" + product.getName() + "\" is sold out."
                    : "Only " + product.getStock() + " of \"" + product.getName() + "\" left in stock.");
        }
    }

    /** A snapshot copy, so pages can loop over it while other requests change the cart. */
    public synchronized List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public synchronized int getItemCount() {
        int count = 0;
        for (CartItem item : items.values()) {
            count += item.getQuantity();
        }
        return count;
    }

    public synchronized BigDecimal getSubtotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (CartItem item : items.values()) {
            sum = sum.add(item.getSubtotal());
        }
        return sum;
    }

    public BigDecimal getShipping() {
        BigDecimal subtotal = getSubtotal();
        if (subtotal.signum() == 0 || subtotal.compareTo(FREE_SHIPPING_MIN) >= 0) {
            return BigDecimal.ZERO;
        }
        return SHIPPING_FEE;
    }

    public BigDecimal getTotal() {
        return getSubtotal().add(getShipping());
    }

    public boolean isFreeShipping() {
        return getSubtotal().compareTo(FREE_SHIPPING_MIN) >= 0;
    }

    public BigDecimal getAmountToFreeShipping() {
        return isFreeShipping() ? BigDecimal.ZERO : FREE_SHIPPING_MIN.subtract(getSubtotal());
    }

    public int getFreeShippingPercent() {
        if (isFreeShipping()) {
            return 100;
        }
        return getSubtotal().multiply(BigDecimal.valueOf(100))
                .divide(FREE_SHIPPING_MIN, 0, RoundingMode.DOWN).intValue();
    }

    public String getSubtotalDisplay() {
        return Money.format(getSubtotal());
    }

    public String getShippingDisplay() {
        return getShipping().signum() == 0 ? "Free" : Money.format(getShipping());
    }

    public String getTotalDisplay() {
        return Money.format(getTotal());
    }

    public String getAmountToFreeShippingDisplay() {
        return Money.format(getAmountToFreeShipping());
    }
}
