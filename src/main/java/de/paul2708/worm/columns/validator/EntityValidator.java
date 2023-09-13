package de.paul2708.worm.columns.validator;

import de.paul2708.worm.columns.Column;
import de.paul2708.worm.columns.Identifier;
import de.paul2708.worm.columns.Table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public final class EntityValidator {

    private EntityValidator() {

    }

    // TODO: Merge methods into one for loop over all fields
    public static void validate(Class<?> clazz) {
        validateIdentifier(clazz);
        validateEmptyConstructor(clazz);
        validateTable(clazz);
        validateNonFinalFields(clazz);
    }

    private static void validateIdentifier(Class<?> clazz) {
        int count = 0;
        boolean columnAndIdentifier = false;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Identifier.class)) {
                count++;

                if (field.isAnnotationPresent(Column.class)) {
                    columnAndIdentifier = true;
                }
            }
        }
        if (count != 1 || !columnAndIdentifier) {
            throw new InvalidEntityException(("Class %s must have exactly one identifier, " +
                    "i.e., a field with annotation @Identifier.").formatted(clazz.getName()));
        }
    }

    private static void validateEmptyConstructor(Class<?> clazz) {
        try {
            clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new InvalidEntityException(("Class %s must have an empty constructor.").formatted(clazz.getName()));
        }
    }

    private static void validateTable(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new InvalidEntityException(("Class %s must have the annotation @Table").formatted(clazz.getName()));
        }
    }

    private static void validateNonFinalFields(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class) && Modifier.isFinal(field.getModifiers())) {
                throw new InvalidEntityException(("Field %s in class %s cannot be final.").formatted(field.getName(),
                        clazz.getName()));
            }
        }
    }
}
