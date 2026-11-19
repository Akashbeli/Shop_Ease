package com.shopease.exception;

/** Thrown when a shopper asks for more units than we have. */
public class OutOfStockException extends ShopException {
    private static final long serialVersionUID = 1L;

    public OutOfStockException(String message) {
        super(message);
    }
}
