package de.paul2708.worm.database.sql.helper;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.ColumnMapper;
import de.paul2708.worm.database.sql.context.ConnectionContext;
import de.paul2708.worm.database.sql.datatypes.ColumnsRegistry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class EntityCreator {

    public static Object fromColumns(Class<?> entityClass, ColumnsRegistry registry, ResultSet resultSet, ColumnMapper mapper, ConnectionContext context) {
        AttributeResolver resolver = new AttributeResolver(entityClass);

        Map<String, Object> foreignFields = new HashMap<>();

        // Create foreign key objects
        for (ColumnAttribute foreignKey : resolver.getForeignKeys()) {
            foreignFields.put(foreignKey.fieldName(), fromColumns(foreignKey.type(), registry, resultSet, mapper, context));
        }

        Map<String, Object> fieldValues = new HashMap<>();
        for (ColumnAttribute column : resolver.getColumns()) {
            if (column.isCollection()) {
                continue;
            }

            fieldValues.put(column.fieldName(), getValue(resultSet, registry, column, column.getFullColumnName(), column.type()));
        }

        for (ColumnAttribute column : resolver.getColumns()) {
            if (!column.isCollection()) {
                continue;
            }

            CollectionSupportTable supportTable = new CollectionSupportTable(resolver, column, registry, mapper, context);
            fieldValues.put(column.fieldName(), supportTable.get(resolver.createInstance(fieldValues)));
        }

        fieldValues.putAll(foreignFields);

        return resolver.createInstance(fieldValues);
    }

    private static Object getValue(ResultSet resultSet, ColumnsRegistry registry, ColumnAttribute attribute,
                                   String column, Class<?> expectedType) {
        try {
            return registry.getDataType(expectedType).from(resultSet, attribute, column);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
