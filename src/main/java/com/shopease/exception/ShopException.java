package com.shopease.exception;

/** Checked exception for business-rule problems. The message is shown to the shopper. */
public class ShopException extends Exception {
    private static final long serialVersionUID = 1L;

    public ShopException(String message) {
        super(message);
    }
}
