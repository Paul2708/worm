package de.paul2708.worm.database;

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
    public void prepare(Class<?> entityClass) {
        System.out.printf("Prepare database for %s%n", entityClass.getName());
    }

    @Override
    public Object save(Object key, Object entity) {
        database.put(key, entity);

        return entity;
    }

    @Override
    public Collection<Object> findAll() {
        return database.values();
    }

    @Override
    public Optional<Object> findById(Object key) {
        return Optional.ofNullable(database.get(key));
    }

    @Override
    public void delete(Object key) {
        database.remove(key);
    }
}