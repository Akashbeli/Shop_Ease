package com.shopease.model;

import java.io.Serializable;

/**
 * Abstract parent of every database-backed class.
 * Shows Abstraction (describe() has no body here) and Inheritance (children reuse the id).
 */
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /** Every subclass describes itself in its own way (Polymorphism). */
    public abstract String describe();
}
