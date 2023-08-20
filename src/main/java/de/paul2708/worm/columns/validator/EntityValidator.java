package de.paul2708.worm.columns.validator;

import de.paul2708.worm.columns.Column;
import de.paul2708.worm.columns.PrimaryKey;
import de.paul2708.worm.columns.Table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public final class EntityValidator {

    private EntityValidator() {

    }

    // TODO: Merge methods into one for loop over all fields
    public static void validate(Class<?> clazz) {
        validatePrimaryKey(clazz);
        validateEmptyConstructor(clazz);
        validateTable(clazz);
        validateNonFinalFields(clazz);
    }

    private static void validatePrimaryKey(Class<?> clazz) {
        int count = 0;
        boolean columnAndPrimaryKey = false;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                count++;

                if (field.isAnnotationPresent(Column.class)) {
                    columnAndPrimaryKey = true;
                }
            }
        }
        if (count != 1 || !columnAndPrimaryKey) {
            throw new InvalidEntityException(("Class %s must have exactly one primary key, " +
                    "i.e., a field with annotation @PrimaryKey.").formatted(clazz.getName()));
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
