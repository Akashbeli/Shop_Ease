package com.shopease.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.shopease.util.Money;

public class Product extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** Icon names the stylesheet can draw (see .ico-* in style.css). */
    public static final List<String> ICON_KEYS = Collections.unmodifiableList(Arrays.asList(
            "bag", "headphones", "phone", "laptop", "watch", "camera", "speaker", "keyboard",
            "mouse", "tv", "shirt", "shoes", "backpack", "sunglasses", "jeans", "dress", "sofa",
            "lamp", "coffee", "cup", "plant", "book", "notebook", "pen", "cricket", "yoga",
            "dumbbell", "bicycle", "game", "gift", "perfume"));

    private String name = "";
    private String description = "";
    private String category = "";
    private BigDecimal price = BigDecimal.ZERO;
    private int stock;
    private double rating = 4.0;
    private String icon = "bag";
    private String imageUrl = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? "" : name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category == null ? "" : category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price == null ? BigDecimal.ZERO : price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = (icon == null || icon.trim().isEmpty()) ? "bag" : icon.trim();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl == null ? "" : imageUrl.trim();
    }

    public boolean isInStock() {
        return stock > 0;
    }

    public boolean isLowStock() {
        return stock > 0 && stock <= 5;
    }

    public String getPriceDisplay() {
        return Money.format(price);
    }

    public String getCategoryClass() {
        return Category.cssClassFor(category);
    }

    public int getTint() {
        return Category.tintFor(category);
    }

    @Override
    public String describe() {
        return name + " (" + category + ") for " + getPriceDisplay();
    }
}
