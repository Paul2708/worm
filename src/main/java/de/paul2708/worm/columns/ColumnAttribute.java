package de.paul2708.worm.columns;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ColumnAttribute implements Comparable<ColumnAttribute> {

    private final String columnName;
    private final String fieldName;
    private final Class<?> type;

    private final List<ColumnProperty> properties;

    public ColumnAttribute(String columnName, String fieldName, Class<?> type) {
        this.columnName = columnName;
        this.fieldName = fieldName;
        this.type = type;

        this.properties = new ArrayList<>();
    }

    public void addProperty(ColumnProperty property) {
        properties.add(property);
    }

    public String columnName() {
        return columnName;
    }

    public String fieldName() {
        return fieldName;
    }

    public Class<?> type() {
        return type;
    }

    public boolean hasMaximumLength() {
        return properties.stream().anyMatch(property -> property instanceof LengthRestrictedProperty);
    }

    public boolean hasProperty(Class<?> propertyClass) {
        for (ColumnProperty property : properties) {
            if (property.getClass().equals(propertyClass)) {
                return true;
            }
        }

        return false;
    }

    public <T> T getProperty(Class<T> propertyClass) {
        for (ColumnProperty property : properties) {
            if (property.getClass().equals(propertyClass)) {
                return (T) property;
            }
        }

        return null;
    }

    @Override
    public int compareTo(ColumnAttribute other) {
        return columnName.compareTo(other.columnName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnAttribute that = (ColumnAttribute) o;
        return Objects.equals(columnName, that.columnName) && Objects.equals(fieldName, that.fieldName) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName, fieldName, type);
    }

    @Override
    public String toString() {
        return "ColumnAttribute{" +
                "columnName='" + columnName + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", type=" + type +
                '}';
    }
}