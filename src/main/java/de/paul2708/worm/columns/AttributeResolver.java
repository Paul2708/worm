package de.paul2708.worm.columns;

import java.lang.reflect.Field;

public class AttributeResolver {

    public final Object object;

    public AttributeResolver(Object object) {
        this.object = object;
    }

    public PrimaryKeyAttribute getPrimaryKey() {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                String column = field.getAnnotation(PrimaryKey.class).value();

                return new PrimaryKeyAttribute(field.getName(), column, field.getType());
            }
        }

        return null;
    }
}
