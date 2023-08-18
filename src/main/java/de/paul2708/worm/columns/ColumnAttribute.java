package de.paul2708.worm.columns;

import de.paul2708.worm.columns.properties.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ColumnAttribute implements Comparable<ColumnAttribute> {

    private final String columnName;
    private final String fieldName;
    private final Class<?> type;
    private final Class<?> entityClass;

    private final List<ColumnProperty> properties;

    public ColumnAttribute(String columnName, String fieldName, Class<?> type, Class<?> entityClass) {
        this.columnName = columnName;
        this.fieldName = fieldName;
        this.type = type;
        this.entityClass = entityClass;

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

    public String getFullColumnName() {
        return "%s.%s".formatted(new AttributeResolver(entityClass).getTable(), columnName);
    }

    public Field getField() {
        try {
            Field field = entityClass.getDeclaredField(fieldName);
            field.setAccessible(true);

            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValue(Object targetObject, Object value) {
        try {
            Field field = targetObject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            field.set(targetObject, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue(Object targetObject) {
        try {
            Field field = targetObject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            return field.get(targetObject);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends ColumnProperty> boolean hasProperty(Class<T> propertyClass) {
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

    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return getField().isAnnotationPresent(annotationClass);
    }

    public boolean hasMaximumLength() {
        return hasProperty(LengthRestrictedProperty.class);
    }

    public boolean isPrimaryKey() {
        return hasProperty(PrimaryKeyProperty.class);
    }

    public boolean isAutoGenerated() {
        return hasProperty(AutoGeneratedProperty.class);
    }

    public boolean isForeignKey() {
        return hasProperty(ForeignKeyProperty.class);
    }

    @Override
    public int compareTo(ColumnAttribute other) {
        if (hasProperty(PrimaryKeyProperty.class) && other.hasProperty(PrimaryKeyProperty.class)) {
            return columnName.compareTo(other.columnName);
        } else if (hasProperty(PrimaryKeyProperty.class)) {
            return -1;
        } else if (other.hasProperty(PrimaryKeyProperty.class)) {
            return 1;
        } else {
            return columnName.compareTo(other.columnName);
        }
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