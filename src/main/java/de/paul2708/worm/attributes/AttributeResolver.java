package de.paul2708.worm.attributes;

import de.paul2708.worm.attributes.properties.*;
import de.paul2708.worm.util.Reflections;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttributeResolver {

    public final Class<?> clazz;

    public AttributeResolver(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getEntity() {
        return clazz.getAnnotation(Entity.class).value();
    }

    public List<AttributeInformation> getAttributes() {
        List<AttributeInformation> attributes = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            AttributeInformation attribute = mapFieldToAttribute(field);
            if (attribute != null) {
                attributes.add(attribute);
            }
        }

        Collections.sort(attributes);

        return attributes;
    }

    public AttributeInformation getIdentifier() {
        return getAttributes().stream()
                .filter(AttributeInformation::isIdentifier)
                .findAny()
                .orElse(null);
    }

    public List<AttributeInformation> getAttributesWithoutIdentifier() {
        return getAttributes().stream()
                .filter(attribute -> !attribute.isIdentifier())
                .sorted()
                .toList();
    }

    public List<AttributeInformation> getReferences() {
        return getAttributes().stream()
                .filter(AttributeInformation::isReference)
                .sorted()
                .toList();
    }

    public String getFormattedEntityNames() {
        String entities = getEntity();

        if (!getReferences().isEmpty()) {
            entities += ", ";
            entities += getReferences().stream()
                    .map(attribute -> attribute.getProperty(ReferenceProperty.class).getReferenceEntity())
                    .collect(Collectors.joining(", "));
        }

        return entities;
    }

    private AttributeInformation mapFieldToAttribute(Field field) {
        if (!field.isAnnotationPresent(Attribute.class)) {
            return null;
        }

        String attributeName = field.getAnnotation(Attribute.class).value();

        AttributeInformation attribute = new AttributeInformation(attributeName, field.getName(), field.getType(), clazz);

        // Map annotations to attribute properties
        if (field.isAnnotationPresent(Identifier.class)) {
            attribute.addProperty(new IdentifierProperty(field.getAnnotation(Identifier.class).generator()));
        }
        if (field.isAnnotationPresent(MaxLength.class)) {
            attribute.addProperty(new LengthRestrictedProperty(field.getAnnotation(MaxLength.class).value()));
        }
        if (field.isAnnotationPresent(Reference.class)) {
            attribute.addProperty(new ReferenceProperty(field.getType()));
        }
        if (field.isAnnotationPresent(TimeZone.class)) {
            attribute.addProperty(new TimeZoneProperty(field.getAnnotation(TimeZone.class).value()));
        }

        return attribute;
    }

    public Object createInstance(Map<String, Object> fieldValues) {
        return Reflections.createInstance(clazz, fieldValues);
    }

    public Class<?> getTargetClass() {
        return clazz;
    }
}
