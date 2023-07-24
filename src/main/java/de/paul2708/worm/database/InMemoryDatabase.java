package de.paul2708.worm.database;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.PrimaryKeyAttribute;
import de.paul2708.worm.repository.actions.*;

import java.lang.reflect.Field;
import java.util.*;

public class InMemoryDatabase implements Database {

    private final Map<Object, Object> database;

    private final KeyGenerator keyGenerator;

    public InMemoryDatabase() {
        this.database = new HashMap<>();

        this.keyGenerator = new KeyGenerator();
    }

    @Override
    public Object process(DatabaseAction action) {
        if (action instanceof SaveAction) {
            Object targetEntity = action.getMethodInformation().args()[0];
            AttributeResolver resolver = new AttributeResolver(targetEntity);

            PrimaryKeyAttribute primaryKey = resolver.getPrimaryKey();
            Object generatedKey = keyGenerator.generate(primaryKey.fieldType());


            try {
                Field field = targetEntity.getClass().getDeclaredField(primaryKey.fieldName());
                field.setAccessible(true);

                field.set(targetEntity, generatedKey);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            database.put(generatedKey, targetEntity);
            return targetEntity;
        } else if (action instanceof FindAllAction) {
            MethodInformation methodInformation = action.getMethodInformation();

            return database.values();
        } else if (action instanceof FindByIdAction) {
            return Optional.ofNullable(database.get(action.getMethodInformation().args()[0]));
        } else if (action instanceof DeleteAction) {
            Object targetEntity = action.getMethodInformation().args()[0];
            AttributeResolver resolver = new AttributeResolver(targetEntity);
            PrimaryKeyAttribute primaryKey = resolver.getPrimaryKey();

            Object id;

            try {
                Field field = targetEntity.getClass().getDeclaredField(primaryKey.fieldName());
                field.setAccessible(true);
                id = field.get(targetEntity);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            if (id == null) {
                throw new IllegalArgumentException("Cannot access primary key");
            }

            database.remove(id);

            return null;
        }

        throw new IllegalArgumentException("No action matches");
    }
}