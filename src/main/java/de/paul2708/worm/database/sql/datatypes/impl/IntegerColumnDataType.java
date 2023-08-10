package de.paul2708.worm.database.sql.datatypes.impl;

import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class IntegerColumnDataType implements ColumnDataType<Integer> {

    @Override
    public boolean matches(Class<?> clazz) {
        return clazz.equals(Integer.class) || clazz.equals(int.class);
    }

    @Override
    public Integer from(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getInt(column);
    }

    @Override
    public void to(PreparedStatement statement, int index, Integer value) throws SQLException {
        statement.setInt(index, value);
    }

    @Override
    public String getSqlType(ColumnAttribute attribute) {
        return "INT";
    }
}
