package de.paul2708.worm.attributes.validator;

import de.paul2708.worm.attributes.Attribute;
import de.paul2708.worm.attributes.Identifier;
import de.paul2708.worm.attributes.Entity;

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
        validateEntity(clazz);
        validateNonFinalFields(clazz);
    }

    private static void validateIdentifier(Class<?> clazz) {
        int count = 0;
        boolean attributeAndIdentifier = false;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Identifier.class)) {
                count++;

                if (field.isAnnotationPresent(Attribute.class)) {
                    attributeAndIdentifier = true;
                }
            }
        }
        if (count != 1 || !attributeAndIdentifier) {
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

    private static void validateEntity(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new InvalidEntityException(("Class %s must have the annotation @Entity").formatted(clazz.getName()));
        }
    }

    private static void validateNonFinalFields(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Attribute.class) && Modifier.isFinal(field.getModifiers())) {
                throw new InvalidEntityException(("Field %s in class %s cannot be final.").formatted(field.getName(),
                        clazz.getName()));
            }
        }
    }
}
