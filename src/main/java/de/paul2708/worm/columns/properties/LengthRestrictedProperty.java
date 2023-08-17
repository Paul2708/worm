package de.paul2708.worm.columns.properties;

public record LengthRestrictedProperty(int length) implements ColumnProperty {

    public boolean isTooLong(Object value) {
        if (value instanceof String text) {
            return text.length() > length;
        }
        if (value instanceof Byte[] bytes) {
            return bytes.length > length;
        }
        if (value instanceof byte[] bytes) {
            return bytes.length > length;
        }
        return false;
    }
}