package de.paul2708.worm.database.sql.helper;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.ColumnMapper;
import de.paul2708.worm.database.sql.context.ConnectionContext;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public final class EntityCreator {

    public static Object fromColumns(Class<?> entityClass, ResultSet resultSet, ColumnMapper mapper, ConnectionContext context) {
        AttributeResolver resolver = new AttributeResolver(entityClass);

        Map<String, Object> foreignFields = new HashMap<>();

        // Create foreign key objects
        for (ColumnAttribute foreignKey : resolver.getForeignKeys()) {
            foreignFields.put(foreignKey.fieldName(), fromColumns(foreignKey.type(), resultSet, mapper, context));
        }

        Map<String, Object> fieldValues = new HashMap<>();
        for (ColumnAttribute column : resolver.getColumns()) {
            if (column.isCollection()) {
                continue;
            }

            fieldValues.put(column.fieldName(), mapper.getValue(resultSet, column, column.getFullColumnName(), column.type()));
        }

        for (ColumnAttribute column : resolver.getColumns()) {
            if (!column.isCollection()) {
                continue;
            }

            CollectionSupportTable supportTable = new CollectionSupportTable(resolver, column, mapper, context);
            fieldValues.put(column.fieldName(), supportTable.get(resolver.createInstance(fieldValues)));
        }

        fieldValues.putAll(foreignFields);

        return resolver.createInstance(fieldValues);
    }
}
