package de.paul2708.worm.columns;

import de.paul2708.worm.columns.properties.*;
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

    public String getTable() {
        return clazz.getAnnotation(Table.class).value();
    }

    public List<ColumnAttribute> getColumns() {
        List<ColumnAttribute> columns = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            ColumnAttribute attribute = mapFieldToColumnAttribute(field);
            if (attribute != null) {
                columns.add(attribute);
            }
        }

        Collections.sort(columns);

        return columns;
    }

    public ColumnAttribute getIdentifier() {
        return getColumns().stream()
                .filter(ColumnAttribute::isIdentifier)
                .findAny()
                .orElse(null);
    }

    public List<ColumnAttribute> getColumnsWithoutIdentifier() {
        return getColumns().stream()
                .filter(column -> !column.isIdentifier())
                .sorted()
                .toList();
    }

    public List<ColumnAttribute> getForeignKeys() {
        return getColumns().stream()
                .filter(ColumnAttribute::isForeignKey)
                .sorted()
                .toList();
    }

    public String getFormattedTableNames() {
        String tables = getTable();

        if (!getForeignKeys().isEmpty()) {
            tables += ", ";
            tables += getForeignKeys().stream()
                    .map(column -> column.getProperty(ForeignKeyProperty.class).getForeignTable())
                    .collect(Collectors.joining(", "));
        }

        return tables;
    }

    private ColumnAttribute mapFieldToColumnAttribute(Field field) {
        if (!field.isAnnotationPresent(Column.class)) {
            return null;
        }

        String columnName = field.getAnnotation(Column.class).value();

        ColumnAttribute attribute = new ColumnAttribute(columnName, field.getName(), field.getType(), clazz);

        // Map annotations to column properties
        if (field.isAnnotationPresent(Identifier.class)) {
            attribute.addProperty(new IdentifierProperty());
        }
        if (field.isAnnotationPresent(MaxLength.class)) {
            attribute.addProperty(new LengthRestrictedProperty(field.getAnnotation(MaxLength.class).value()));
        }
        if (field.isAnnotationPresent(AutoGenerated.class)) {
            attribute.addProperty(new AutoGeneratedProperty(field.getAnnotation(AutoGenerated.class).value()));
        }
        if (field.isAnnotationPresent(ForeignKey.class)) {
            attribute.addProperty(new ForeignKeyProperty(field.getType()));
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
