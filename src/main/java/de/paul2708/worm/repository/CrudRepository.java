package de.paul2708.worm.repository;

import java.util.Collection;

public interface CrudRepository<T, K> {

    Collection<T> findAll();

    T findById(K key);

    void delete(T entity);

    T save(T entity);
}