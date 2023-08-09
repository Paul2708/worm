package de.paul2708.worm.util;

import java.lang.reflect.Field;

public final class DefaultValueChecker {

    public static boolean isDefaultValue(Object object, Field field) {
        Class<?> clazz = field.getType();
        Object value;
        try {
            value = field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (clazz.equals(boolean.class)) {
            return Boolean.FALSE.equals(value);
        }
        if (clazz.equals(char.class)) {
            return (Character) value == 0;
        }
        if (clazz.isPrimitive()) {
            return ((Number) value).doubleValue() == 0;
        }

        return value == null;
    }
}
