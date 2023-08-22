package de.paul2708.worm.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class Reflections {

    public static Class<?> getElementType(Field listField) {
        ParameterizedType parameterizedType = (ParameterizedType) listField.getGenericType();

        return (Class<?>) parameterizedType.getActualTypeArguments()[0];
    }

    public static Field getField(Class<?> clazz, String field) {
        try {
            return clazz.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
