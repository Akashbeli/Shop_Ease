package com.shopease.model;

import java.io.Serializable;
import java.util.Locale;

/** A product category with a product count and a representative icon (not a table row). */
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final int count;
    private final String icon;

    public Category(String name, int count, String icon) {
        this.name = name;
        this.count = count;
        this.icon = (icon == null || icon.isEmpty()) ? "bag" : icon;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public String getIcon() {
        return icon;
    }

    public String getCountLabel() {
        return count == 1 ? "1 product" : count + " products";
    }

    public String getCssClass() {
        return cssClassFor(name);
    }

    public int getTint() {
        return tintFor(name);
    }

    /** "Home & Kitchen" becomes "cat-home-kitchen" (used by the stylesheet for colour). */
    public static String cssClassFor(String categoryName) {
        String slug = categoryName == null ? "other"
                : categoryName.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        return "cat-" + (slug.isEmpty() ? "other" : slug);
    }

    /** A stable fallback colour (0-5) for categories the stylesheet does not know. */
    public static int tintFor(String categoryName) {
        return categoryName == null ? 0 : (categoryName.hashCode() & 0x7fffffff) % 6;
    }
}
