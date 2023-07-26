package de.paul2708.worm.database.memory;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.database.Database;

import java.util.*;

public class InMemoryDatabase implements Database {

    private final Map<Object, Object> database;

    public InMemoryDatabase() {
        this.database = new HashMap<>();
    }

    @Override
    public void connect() {
        System.out.println("Create local in-memory database.");
    }

    @Override
    public void prepare(AttributeResolver resolver) {
        System.out.printf("Prepare database for %s%n", resolver.getTable());
    }

    @Override
    public Object save(AttributeResolver resolver, Object key, Object entity) {
        database.put(key, entity);

        return entity;
    }

    @Override
    public Collection<Object> findAll(AttributeResolver resolver) {
        return database.values();
    }

    @Override
    public Optional<Object> findById(AttributeResolver resolver, Object key) {
        return Optional.ofNullable(database.get(key));
    }

    @Override
    public void delete(AttributeResolver resolver, Object entity) {
        database.remove(entity);
    }
}