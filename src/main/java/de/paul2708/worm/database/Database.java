package de.paul2708.worm.database;

import java.util.Collection;
import java.util.Optional;

public interface Database {

    void connect();

    // TODO: Replace entityClass with attribute resolver
    void prepare(Class<?> entityClass);

    Object save(Object key, Object entity);

    Collection<Object> findAll();

    Optional<Object> findById(Object key);

    void delete(Object key);
}
