package de.paul2708.worm.database;

import java.util.Collection;
import java.util.Optional;

public interface Database {

    Object save(Object key, Object entity);

    Collection<Object> findAll();

    Optional<Object> findById(Object key);

    void delete(Object key);
}
