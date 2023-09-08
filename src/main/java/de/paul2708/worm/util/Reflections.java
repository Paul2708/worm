package de.paul2708.worm.util;

import java.lang.reflect.*;
import java.util.*;

public class Reflections {

    public static Class<?> getElementType(Field listField) {
        ParameterizedType parameterizedType = (ParameterizedType) listField.getGenericType();

        return (Class<?>) parameterizedType.getActualTypeArguments()[0];
    }

    public static List<Class<?>> getElementTypes(Field mapField) {
        ParameterizedType parameterizedType = (ParameterizedType) mapField.getGenericType();

        List<Class<?>> params = new LinkedList<>();

        for (Type actualTypeArgument : parameterizedType.getActualTypeArguments()) {
            params.add((Class<?>) actualTypeArgument);
        }

        return params;
    }

    public static Class<?> getElementTypeFromArray(Field arrayField) {
        Class<?> arrayClass = arrayField.getType();
        if (!arrayClass.isArray()) {
            throw new IllegalArgumentException("The given class %s is not a class of an array.".formatted(arrayClass));
        }

        return arrayClass.getComponentType();
    }

    public static boolean isList(Class<?> listImplClass) {
        return hasInterface(listImplClass, List.class);
    }

    public static boolean isSet(Class<?> setImplClass) {
        return hasInterface(setImplClass, Set.class);
    }

    public static boolean isMap(Class<?> mapImplClass) {
        return hasInterface(mapImplClass, Map.class);
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

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);

            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFieldValue(String fieldName, Object object, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            field.set(object, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object createInstance(Class<?> clazz, Map<String, Object> fieldValues) {
        try {
            Object object = clazz.getConstructor().newInstance();

            for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
                setFieldValue(entry.getKey(), object, entry.getValue());
            }

            return object;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
