package de.paul2708.worm.database;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;
import de.paul2708.worm.database.sql.datatypes.ColumnsRegistry;

import java.util.Collection;
import java.util.Optional;

public interface Database {

    void connect();

    // TODO: Add disconnect

    void prepare(AttributeResolver resolver);

    Object save(AttributeResolver resolver, Object key, Object entity);

    Collection<Object> findAll(AttributeResolver resolver);

    Optional<Object> findById(AttributeResolver resolver, Object key);

    void delete(AttributeResolver resolver, Object entity);
}
