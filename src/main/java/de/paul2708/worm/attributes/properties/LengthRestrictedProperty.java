package de.paul2708.worm.attributes.properties;

import java.lang.reflect.Array;
import java.util.Collection;

public record LengthRestrictedProperty(int length) implements AttributeProperty {

    public boolean exceedsLength(Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof String string) {
            return string.length() > length;
        }
        if (object.getClass().isArray()) {
            return Array.getLength(object) > length;
        }
        if (object instanceof Collection<?> collection) {
            return collection.size() > length;
        }

        throw new IllegalArgumentException("The field is annotated with @MaxLength but it has no length.");
    }
}