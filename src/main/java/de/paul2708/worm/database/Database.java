package de.paul2708.worm.database;

import de.paul2708.worm.columns.AttributeResolver;

import java.util.Collection;
import java.util.Optional;

public interface Database {

    void connect();

    void prepare(AttributeResolver resolver);

    Object save(Object key, Object entity);

    Collection<Object> findAll(AttributeResolver resolver);

    Optional<Object> findById(Object key);

    void delete(Object key);
}
