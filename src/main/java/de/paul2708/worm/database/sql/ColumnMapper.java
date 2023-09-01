package de.paul2708.worm.database.sql;

import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.columns.properties.ForeignKeyProperty;
import de.paul2708.worm.database.sql.datatypes.ColumnsRegistry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ColumnMapper {

    private final ColumnsRegistry registry;

    public ColumnMapper(ColumnsRegistry registry) {
        this.registry = registry;
    }

    public void setParameterValue(ColumnAttribute column, Object entity, PreparedStatement statement, int index) {
        if (column.isForeignKey()) {
            ColumnAttribute foreignPrimaryKey = column.getProperty(ForeignKeyProperty.class).getForeignPrimaryKey();
            Object value = foreignPrimaryKey.getValue(column.getValue(entity));

            setValue(statement, value.getClass(), index, column, value);
        } else {
            setValue(statement, column.type(), index, column, column.getValue(entity));
        }
    }

    public void setDirectParameterValue(ColumnAttribute column, Object columnValue, PreparedStatement statement, int index) {
        setValue(statement, column.type(), index, column, columnValue);
    }

    public void setDirectParameterValue(Class<?> columnClass, Object columnValue, PreparedStatement statement, int index) {
        try {
            registry.getDataType(columnClass).unsafeTo(statement, index, null, columnValue);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setValue(PreparedStatement statement, Class<?> expectedType, int index, ColumnAttribute attribute, Object value) {
        try {
            registry.getDataType(expectedType).unsafeTo(statement, index, attribute, value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue(ResultSet resultSet, ColumnAttribute attribute, String column, Class<?> expectedType) {
        try {
            return registry.getDataType(expectedType).from(resultSet, attribute, column);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue(ResultSet resultSet, ColumnAttribute attribute) {
        try {
            return registry.getDataType(attribute.type()).from(resultSet, attribute, attribute.columnName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue(ResultSet resultSet, String column, Class<?> expectedType) {
        return this.getValue(resultSet, null, column, expectedType);
    }

    public String toSqlType(ColumnAttribute column) {
        Class<?> type = column.type();

        return registry.getDataType(type).getSqlType(column);
    }

    public String toSqlType(Class<?> clazz) {
        return registry.getDataType(clazz).getSqlType(null);
    }
}
