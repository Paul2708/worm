package de.paul2708.worm.database.sql.datatypes.impl;

import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class LongColumnDataType implements ColumnDataType<Long> {

    @Override
    public boolean matches(Class<?> clazz) {
        return clazz.equals(Long.class) || clazz.equals(long.class);
    }

    @Override
    public Long from(ResultSet resultSet, ColumnAttribute attribute, String column) throws SQLException {
        return resultSet.getLong(column);
    }

    @Override
    public void to(PreparedStatement statement, int index, ColumnAttribute attribute, Long value) throws SQLException {
        statement.setLong(index, value);
    }

    @Override
    public String getSqlType(ColumnAttribute attribute) {
        return "BIGINT";
    }
}
