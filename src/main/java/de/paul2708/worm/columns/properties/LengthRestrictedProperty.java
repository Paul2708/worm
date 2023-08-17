package de.paul2708.worm.columns.properties;

import java.lang.reflect.Array;

public record LengthRestrictedProperty(int length) implements ColumnProperty {

    public boolean isTooLong(Object value) {
        if (value instanceof String text) {
            return text.length() > length;
        } else if (value.getClass().isArray()) {
            return Array.getLength(value) > length;
        }
        return false;
    }
}