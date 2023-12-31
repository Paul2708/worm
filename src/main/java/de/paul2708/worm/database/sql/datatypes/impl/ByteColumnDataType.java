package de.paul2708.worm.database.sql.datatypes.impl;

import de.paul2708.worm.attributes.AttributeInformation;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ByteColumnDataType implements ColumnDataType<Byte> {

    @Override
    public boolean matches(Class<?> clazz) {
        return clazz.equals(Byte.class) || clazz.equals(byte.class);
    }

    @Override
    public Byte from(ResultSet resultSet, AttributeInformation attribute, String column) throws SQLException {
        return resultSet.getByte(column);
    }

    @Override
    public void to(PreparedStatement statement, int index, AttributeInformation attribute, Byte value) throws SQLException {
        statement.setByte(index, value);
    }

    @Override
    public String getSqlType(AttributeInformation attribute) {
        return "TINYINT";
    }
}
