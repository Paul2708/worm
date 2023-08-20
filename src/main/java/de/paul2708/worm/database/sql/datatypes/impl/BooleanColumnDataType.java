package de.paul2708.worm.database.sql.datatypes.impl;

import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class BooleanColumnDataType implements ColumnDataType<Boolean> {

    @Override
    public boolean matches(Class<?> clazz) {
        return clazz.equals(Boolean.class) || clazz.equals(boolean.class);
    }

    @Override
    public Boolean from(ResultSet resultSet, ColumnAttribute attribute, String column) throws SQLException {
        return resultSet.getBoolean(column);
    }

    @Override
    public void to(PreparedStatement statement, int index, ColumnAttribute attribute, Boolean value) throws SQLException {
        statement.setBoolean(index, value);
    }

    @Override
    public String getSqlType(ColumnAttribute attribute) {
        return "BOOLEAN";
    }
}
