package com.shopease.exception;

/** Unchecked wrapper around SQLException so controllers stay free of try/catch noise. */
public class DataAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
