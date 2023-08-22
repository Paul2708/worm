package de.paul2708.worm.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;

public class Reflections {

    public static Class<?> getElementType(Field listField) {
        ParameterizedType parameterizedType = (ParameterizedType) listField.getGenericType();

        return (Class<?>) parameterizedType.getActualTypeArguments()[0];
    }

    public static boolean isList(Class<?> listImplClass) {
        return hasInterface(listImplClass, List.class);
    }

    public static boolean isSet(Class<?> setImplClass) {
        return hasInterface(setImplClass, Set.class);
    }

    private static boolean hasInterface(Class<?> implClass, Class<?> interfaceClass) {
        if (implClass == null) {
            return false;
        }
        if (implClass.equals(interfaceClass)) {
            return true;
        }

        for (Class<?> anInterface : implClass.getInterfaces()) {
            if (anInterface.equals(interfaceClass)) {
                return true;
            }
        }

        return hasInterface(implClass.getSuperclass(), interfaceClass);
    }

    public static Field getField(Class<?> clazz, String field) {
        try {
            return clazz.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
