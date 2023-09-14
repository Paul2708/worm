package de.paul2708.worm.database.sql;

import de.paul2708.worm.attributes.AttributeInformation;
import de.paul2708.worm.attributes.properties.ReferenceProperty;
import de.paul2708.worm.database.sql.datatypes.ColumnsRegistry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ColumnMapper {

    private final ColumnsRegistry registry;

    public ColumnMapper(ColumnsRegistry registry) {
        this.registry = registry;
    }

    public void setParameterValue(AttributeInformation column, Object entity, PreparedStatement statement, int index) {
        if (column.isReference()) {
            AttributeInformation foreignPrimaryKey = column.getProperty(ReferenceProperty.class).getForeignIdentifier();
            Object value = foreignPrimaryKey.getValue(column.getValue(entity));

            setValue(statement, value.getClass(), index, column, value);
        } else {
            setValue(statement, column.type(), index, column, column.getValue(entity));
        }
    }

    public void setDirectParameterValue(AttributeInformation column, Object columnValue, PreparedStatement statement, int index) {
        setValue(statement, column.type(), index, column, columnValue);
    }

    public void setDirectParameterValue(Class<?> columnClass, Object columnValue, PreparedStatement statement, int index) {
        try {
            registry.getDataType(columnClass).unsafeTo(statement, index, null, columnValue);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setValue(PreparedStatement statement, Class<?> expectedType, int index, AttributeInformation attribute, Object value) {
        try {
            registry.getDataType(expectedType).unsafeTo(statement, index, attribute, value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue(ResultSet resultSet, AttributeInformation attribute, String column, Class<?> expectedType) {
        try {
            return registry.getDataType(expectedType).from(resultSet, attribute, column);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue(ResultSet resultSet, AttributeInformation attribute) {
        try {
            return registry.getDataType(attribute.type()).from(resultSet, attribute, attribute.attributeName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue(ResultSet resultSet, String column, Class<?> expectedType) {
        return this.getValue(resultSet, null, column, expectedType);
    }

    public String toSqlType(AttributeInformation column) {
        Class<?> type = column.type();

        return registry.getDataType(type).getSqlType(column);
    }

    public String toSqlType(Class<?> clazz) {
        return registry.getDataType(clazz).getSqlType(null);
    }
}
