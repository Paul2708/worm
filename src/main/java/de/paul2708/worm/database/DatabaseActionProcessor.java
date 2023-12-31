package de.paul2708.worm.database;

import de.paul2708.worm.attributes.AttributeResolver;
import de.paul2708.worm.attributes.AttributeInformation;
import de.paul2708.worm.attributes.generator.ValueGenerator;
import de.paul2708.worm.attributes.properties.IdentifierProperty;
import de.paul2708.worm.attributes.properties.ReferenceProperty;
import de.paul2708.worm.attributes.properties.LengthRestrictedProperty;
import de.paul2708.worm.repository.CrudRepository;
import de.paul2708.worm.repository.Repository;
import de.paul2708.worm.repository.actions.*;
import de.paul2708.worm.util.DefaultValueChecker;
import de.paul2708.worm.util.Reflections;

import java.lang.reflect.Field;
import java.util.*;

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

            // Generate values
            for (AttributeInformation attributes : resolver.getAttributes()) {
                if (attributes.isAutoGenerated() && DefaultValueChecker.isDefaultValue(targetEntity, attributes.getField())) {
                    ValueGenerator<?> generator = attributes.getProperty(IdentifierProperty.class).createGenerator();
                    Object generatedValue = generator.generate();

                    attributes.setValue(targetEntity, generatedValue);
                }
            }

            // Check max length
            for (AttributeInformation attribute : resolver.getAttributes()) {
                if (attribute.hasMaximumLength()) {
                    boolean tooLong = attribute.getProperty(LengthRestrictedProperty.class).exceedsLength(attribute
                            .getValue(targetEntity));

                    if (tooLong) {
                        throw new IllegalStateException("The value of field %s is too long.".formatted(attribute.fieldName()));
                    }
                }
            }

            // Handle references
            for (AttributeInformation attribute : resolver.getAttributes()) {
                if (attribute.isReference()) {
                    CrudRepository referenceRepository = Repository.create(CrudRepository.class, attribute.getProperty(ReferenceProperty.class).getReference(),
                            database);
                    Object referenceEntity = referenceRepository.save(attribute.getValue(targetEntity));

                    attribute.setValue(targetEntity, referenceEntity);
                }
            }

            return database.save(resolver, targetEntity);
        } else if (action instanceof FindAllAction) {
            return database.findAll(new AttributeResolver(entityClass));
        } else if (action instanceof FindByIdAction) {
            return database.findById(resolver, action.getMethodInformation().args()[0]);
        } else if (action instanceof DeleteAction) {
            Object targetEntity = action.getMethodInformation().args()[0];

            Object key = getField(resolver.getIdentifier().fieldName(), targetEntity);

            if (key == null) {
                throw new IllegalArgumentException("Cannot access identifier");
            }

            database.delete(resolver, targetEntity);

            return null;
        } else if (action instanceof FindByAttributesAction) {
            String methodName = action.getMethodInformation().method().getName();
            String[] attributeNames = methodName.replace("findBy", "").split("And");
            Map<AttributeInformation, Object> attributes = new HashMap<>();

            for (int i = 0; i < attributeNames.length; i++) {
                AttributeInformation attribute = getAttributeByTransformedName(resolver, attributeNames[i]);
                attributes.put(attribute, action.getMethodInformation().args()[i]);
            }

            Collection<Object> entities = database.findByAttributes(resolver, attributes);
            Class<?> returnType = action.getMethodInformation().method().getReturnType();

            if (Reflections.isList(returnType)) {
                return new ArrayList<>(entities);
            } else if (Reflections.isSet(returnType)) {
                return new HashSet<>(entities);
            } else if (returnType.equals(Optional.class)) {
                if (entities.isEmpty()) {
                    return Optional.empty();
                }
                if (entities.size() == 1) {
                    return Optional.of(entities.iterator().next());
                }

                throw new RuntimeException("Expected %s but retrieved multiple entities".formatted(Optional.class));
            }
        }

        throw new IllegalArgumentException("Did not handle database action %s".formatted(action.getClass().getName()));
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

    private AttributeInformation getAttributeByTransformedName(AttributeResolver resolver, String transformedName) {
        for (AttributeInformation attribute : resolver.getAttributes()) {
            if (attribute.getTransformedAttributeName().equals(transformedName)) {
                return attribute;
            }
        }

        throw new IllegalArgumentException("Could not find a matching attribute with the name %s"
                .formatted(transformedName));
    }
}
