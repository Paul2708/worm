package de.paul2708.worm.database.sql.datatypes.impl;

import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;
import de.paul2708.worm.database.sql.datatypes.UUIDConverter;

import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class UUIDColumnDataType implements ColumnDataType<UUID> {

    @Override
    public boolean matches(Class<?> clazz) {
        return clazz.equals(UUID.class);
    }

    @Override
    public UUID from(ResultSet resultSet, String column) throws SQLException {
        return UUIDConverter.convert(resultSet.getBytes(column));
    }

    @Override
    public void to(PreparedStatement statement, int index, UUID value) throws SQLException {
        statement.setBytes(index, UUIDConverter.convert(value));
    }

    @Override
    public String getSqlType(ColumnAttribute attribute) {
        return "BINARY(16)";
    }
}
