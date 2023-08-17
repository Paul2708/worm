package de.paul2708.worm.database.sql.datatypes.impl;

import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ByteArrayColumnDataType implements ColumnDataType<byte[]> {

    @Override
    public boolean matches(Class<?> clazz) {
        return clazz.equals(Byte[].class) || clazz.equals(byte[].class);
    }

    @Override
    public byte[] from(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getBytes(column);
    }

    @Override
    public void to(PreparedStatement statement, int index, byte[] value) throws SQLException {
        statement.setBytes(index, value);
    }

    @Override
    public String getSqlType(ColumnAttribute attribute) {
        return "BLOB";
    }
}
