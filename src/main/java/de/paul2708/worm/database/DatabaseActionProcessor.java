package de.paul2708.worm.database;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.repository.actions.*;

import java.lang.reflect.Field;

public class DatabaseActionProcessor {

    private final Database database;
    private final Class<?> entityClass;

    public DatabaseActionProcessor(Database database, Class<?> entityClass) {
        this.database = database;
        this.entityClass = entityClass;
    }

    public Object process(DatabaseAction action) {
        AttributeResolver resolver = new AttributeResolver(entityClass);

        if (action instanceof SaveAction) {
            Object targetEntity = action.getMethodInformation().args()[0];

            ColumnAttribute primaryKey = resolver.getPrimaryKey();
            Object key = resolver.getValueByColumn(targetEntity, primaryKey.columnName());

            return database.save(resolver, key, targetEntity);
        } else if (action instanceof FindAllAction) {
            return database.findAll(new AttributeResolver(entityClass));
        } else if (action instanceof FindByIdAction) {
            return database.findById(resolver, action.getMethodInformation().args()[0]);
        } else if (action instanceof DeleteAction) {
            Object targetEntity = action.getMethodInformation().args()[0];

            Object key = getField(resolver.getPrimaryKey().fieldName(), targetEntity);

            if (key == null) {
                throw new IllegalArgumentException("Cannot access primary key");
            }

            database.delete(resolver, targetEntity);

            return null;
        }

        throw new IllegalArgumentException("Did not handle database action %s".formatted(action.getClass().getName()));
    }

    private void setField(String fieldName, Object object, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            field.set(object, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getField(String fieldName, Object object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            return field.get(object);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
