package de.paul2708.worm.database.sql.datatypes.impl;

import de.paul2708.worm.attributes.AttributeInformation;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;
import de.paul2708.worm.util.UUIDConverter;

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
    public UUID from(ResultSet resultSet, AttributeInformation attribute, String column) throws SQLException {
        return UUIDConverter.convert(resultSet.getBytes(column));
    }

    @Override
    public void to(PreparedStatement statement, int index, AttributeInformation attribute, UUID value) throws SQLException {
        statement.setBytes(index, UUIDConverter.convert(value));
    }

    @Override
    public String getSqlType(AttributeInformation attribute) {
        return "BINARY(16)";
    }
}
