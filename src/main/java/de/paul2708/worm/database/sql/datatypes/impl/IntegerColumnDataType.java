package de.paul2708.worm.database.sql.datatypes.impl;

import de.paul2708.worm.attributes.AttributeInformation;
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
    public Integer from(ResultSet resultSet, AttributeInformation attribute, String column) throws SQLException {
        return resultSet.getInt(column);
    }

    @Override
    public void to(PreparedStatement statement, int index, AttributeInformation attribute, Integer value) throws SQLException {
        statement.setInt(index, value);
    }

    @Override
    public String getSqlType(AttributeInformation attribute) {
        return "INT";
    }
}
