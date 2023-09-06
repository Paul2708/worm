package de.paul2708.worm.database.memory;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.columns.CreatedAt;
import de.paul2708.worm.columns.UpdatedAt;
import de.paul2708.worm.database.Database;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryDatabase implements Database {

    private final Map<Class<?>, Map<Object, Object>> database;

    public InMemoryDatabase() {
        this.database = new HashMap<>();
    }

    @Override
    public void connect() {
        // Nothing to do here
    }

    @Override
    public void prepare(AttributeResolver resolver) {
        // Nothing to do here
    }

    @Override
    public Object save(AttributeResolver resolver, Object entity) {
        Map<Object, Object> map = database.getOrDefault(resolver.getTargetClass(), new HashMap<>());

        Object primaryKey = resolver.getPrimaryKey().getValue(entity);

        for (ColumnAttribute column : resolver.getColumns()) {
            if (column.hasAnnotation(UpdatedAt.class)
                    || column.hasAnnotation(CreatedAt.class) && !map.containsKey(primaryKey)) {
                column.setValue(entity, LocalDateTime.now());
            }
        }

        map.put(resolver.getPrimaryKey().getValue(entity), entity);
        database.put(resolver.getTargetClass(), map);

        return entity;
    }

    @Override
    public Collection<Object> findAll(AttributeResolver resolver) {
        return database.getOrDefault(resolver.getTargetClass(), new HashMap<>()).values();
    }

    @Override
    public Optional<Object> findById(AttributeResolver resolver, Object key) {
        Map<Object, Object> map = database.getOrDefault(resolver.getTargetClass(), new HashMap<>());

        return Optional.ofNullable(map.get(key));
    }

    @Override
    public Collection<Object> findByAttributes(AttributeResolver resolver, Map<ColumnAttribute, Object> attributes) {
        Map<Object, Object> map = database.getOrDefault(resolver.getTargetClass(), new HashMap<>());
        List<Object> result = new ArrayList<>();

        for (Object entity : map.values()) {
            if (hasMatchingAttributes(resolver, entity, attributes)) {
                result.add(entity);
            }
        }

        return result;
    }

    @Override
    public void delete(AttributeResolver resolver, Object entity) {
        Map<Object, Object> map = database.getOrDefault(resolver.getTargetClass(), new HashMap<>());

        if (map.containsValue(entity)) {
            Object key = null;
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                if (entry.getValue().equals(entity)) {
                    key = entry.getKey();
                    break;
                }
            }

            if (key != null) {
                map.remove(key);
                database.put(resolver.getTargetClass(), map);
            }
        }
    }

    private boolean hasMatchingAttributes(AttributeResolver resolver, Object entity,
                                          Map<ColumnAttribute, Object> attributes) {
        for (ColumnAttribute column : resolver.getColumns()) {
            if (attributes.containsKey(column)) {
                Object actualValue = column.getValue(entity);

                if (!actualValue.equals(attributes.get(column))) {
                    return false;
                }
            }
        }

        return true;
    }
}