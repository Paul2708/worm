package de.paul2708.worm.database.sql.datatypes.impl;

import de.paul2708.worm.attributes.AttributeInformation;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ShortColumnDataType implements ColumnDataType<Short> {

    @Override
    public boolean matches(Class<?> clazz) {
        return clazz.equals(Short.class) || clazz.equals(short.class);
    }

    @Override
    public Short from(ResultSet resultSet, AttributeInformation attribute, String column) throws SQLException {
        return resultSet.getShort(column);
    }

    @Override
    public void to(PreparedStatement statement, int index, AttributeInformation attribute, Short value) throws SQLException {
        statement.setShort(index, value);
    }

    @Override
    public String getSqlType(AttributeInformation attribute) {
        return "SMALLINT";
    }
}
