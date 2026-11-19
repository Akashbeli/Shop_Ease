package com.shopease.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.shopease.util.Money;

/** One line of a placed order. Name and price are copied so history never changes. */
public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int productId;
    private String productName = "";
    private String icon = "bag";
    private BigDecimal unitPrice = BigDecimal.ZERO;
    private int quantity;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? "" : productName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = (icon == null || icon.isEmpty()) ? "bag" : icon;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice == null ? BigDecimal.ZERO : unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public String getUnitPriceDisplay() {
        return Money.format(unitPrice);
    }

    public String getSubtotalDisplay() {
        return Money.format(getSubtotal());
    }

    public int getTint() {
        return Category.tintFor(productName);
    }
}
