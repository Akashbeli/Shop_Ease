package com.shopease.dao;

import java.util.List;

/** Generic data-access contract (Interface + Generics). Every table gets its own implementation. */
public interface Dao<T> {

    /** @return the row as an object, or null when no row has this id */
    T findById(int id);

    List<T> findAll();
}
