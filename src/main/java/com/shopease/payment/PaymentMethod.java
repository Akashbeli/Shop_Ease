package com.shopease.payment;

import com.shopease.model.OrderStatus;

/** One way to pay. Each implementation decides what happens to a fresh order (Polymorphism). */
public interface PaymentMethod {

    String getCode();

    String getLabel();

    String getHint();

    /** Name of the icon drawn on the checkout screen. */
    String getIcon();

    /** Status of the order right after checkout. */
    OrderStatus getInitialStatus();
}
