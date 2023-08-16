package de.paul2708.worm.database.sql.helper;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.datatypes.ColumnsRegistry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class EntityCreator {

    public static Object fromColumns(Class<?> entityClass, ColumnsRegistry registry, ResultSet resultSet) {
        AttributeResolver resolver = new AttributeResolver(entityClass);

        Map<String, Object> foreignFields = new HashMap<>();

        // Create foreign key objects
        for (ColumnAttribute foreignKey : resolver.getForeignKeys()) {
            AttributeResolver foreignResolver = new AttributeResolver(foreignKey.type());

            Map<String, Object> fieldValues = new HashMap<>();
            for (ColumnAttribute column : foreignResolver.getColumns()) {
                fieldValues.put(column.fieldName(), getValue(resultSet, registry, column.getFullColumnName(), column.type()));
            }

            foreignFields.put(foreignKey.fieldName(), foreignResolver.createInstance(fieldValues));
        }

        Map<String, Object> fieldValues = new HashMap<>();
        for (ColumnAttribute column : resolver.getColumns()) {
            fieldValues.put(column.fieldName(), getValue(resultSet, registry, column.getFullColumnName(), column.type()));
        }

        fieldValues.putAll(foreignFields);

        return resolver.createInstance(fieldValues);
    }

    private static Object getValue(ResultSet resultSet, ColumnsRegistry registry, String column, Class<?> expectedType) {
        try {
            return registry.getDataType(expectedType).from(resultSet, column);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
