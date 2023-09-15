package de.paul2708.worm.database;

import de.paul2708.worm.attributes.AttributeResolver;
import de.paul2708.worm.attributes.AttributeInformation;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface Database {

    void connect();

    // TODO: Add disconnect

    void prepare(AttributeResolver resolver);

    Object save(AttributeResolver resolver, Object entity);

    Collection<Object> findAll(AttributeResolver resolver);

    Optional<Object> findById(AttributeResolver resolver, Object key);

    Collection<Object> findByAttributes(AttributeResolver resolver, Map<AttributeInformation, Object> attributes);

    void delete(AttributeResolver resolver, Object entity);
}
