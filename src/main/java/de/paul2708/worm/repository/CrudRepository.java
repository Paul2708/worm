package de.paul2708.worm.repository;

import java.util.Collection;
import java.util.Optional;

public interface CrudRepository<T, K> {

    Collection<T> findAll();

    Optional<T> findById(K key);

    void delete(T entity);

    T save(T entity);
}