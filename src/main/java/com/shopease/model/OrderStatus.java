package com.shopease.model;

/** An enum can carry data: each status knows its label and the colour tone used on the page. */
public enum OrderStatus {
    PLACED("Placed", "info"),
    PAID("Paid", "good"),
    SHIPPED("Shipped", "warn"),
    DELIVERED("Delivered", "good"),
    CANCELLED("Cancelled", "bad");

    private final String label;
    private final String tone;

    OrderStatus(String label, String tone) {
        this.label = label;
        this.tone = tone;
    }

    public String getLabel() {
        return label;
    }

    public String getTone() {
        return tone;
    }
}
