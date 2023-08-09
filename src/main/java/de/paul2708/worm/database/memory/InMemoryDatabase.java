package de.paul2708.worm.database.memory;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.datatypes.ColumnDataType;
import de.paul2708.worm.columns.datatypes.ColumnsRegistry;
import de.paul2708.worm.database.Database;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryDatabase implements Database {

    private final Map<Class<?>, Map<Object, Object>> database;

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
	public void registerColumnsRegistry(ColumnsRegistry registry) {
		throw new UnsupportedOperationException("In-memory database does not support column registry");
	}

	@Override
	public void registerDataType(ColumnDataType<?> dataType) {
		throw new UnsupportedOperationException("In-memory database does not support column registry");
	}

	@Override
    public Object save(AttributeResolver resolver, Object key, Object entity) {
        Map<Object, Object> map = database.getOrDefault(resolver.getTargetClass(), new HashMap<>());

        map.put(key, entity);
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
}