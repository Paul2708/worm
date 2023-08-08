package de.paul2708.worm.columns;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AttributeResolver {

    public final Class<?> clazz;

    public AttributeResolver(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getTable() {
        return clazz.getAnnotation(Table.class).value();
    }

    public PrimaryKeyAttribute getPrimaryKey() {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class) && field.isAnnotationPresent(PrimaryKey.class)) {
                String column = field.getAnnotation(Column.class).value();

                return new PrimaryKeyAttribute(field.getName(), column, field.getType(),
                        field.isAnnotationPresent(AutoGenerated.class));
            }
        }

        return null;
    }

    public List<ColumnAttribute> getColumns() {
        List<ColumnAttribute> columns = new ArrayList<>();

        columns.add(this.getPrimaryKey());
        columns.addAll(getColumnsWithoutPrimaryKey());

        Collections.sort(columns);

        return columns;
    }

    public List<ColumnAttribute> getColumnsWithoutPrimaryKey() {
        List<ColumnAttribute> columns = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(PrimaryKey.class)) {
                String column = field.getAnnotation(Column.class).value();

                ColumnAttribute columnAttribute = new ColumnAttribute(column, field.getName(), field.getType());;
                if (field.getType().equals(String.class) && field.isAnnotationPresent(MaxLength.class)) {
                    columnAttribute.addProperty(new LengthRestrictedProperty(field.getAnnotation(MaxLength.class).value()));
                }

                columns.add(columnAttribute);
            }
        }

        Collections.sort(columns);

        return columns;
    }

    public Object getValueByColumn(Object object, String column) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                if (field.getAnnotation(Column.class).value().equals(column)) {
                    try {
                        field.setAccessible(true);
                        return field.get(object);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return null;
    }

    public void setField(String fieldName, Object object, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            field.set(object, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public Object createInstance(Map<String, Object> fieldValues) {
        try {
            Object object = clazz.getConstructor().newInstance();

            for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
                setField(entry.getKey(), object, entry.getValue());
            }

            return object;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getTargetClass() {
        return clazz;
    }
}
