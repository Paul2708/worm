package de.paul2708.worm.database.sql.datatypes.impl;

import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public final class LocalDateTimeColumnDataType implements ColumnDataType<LocalDateTime> {

    @Override
    public boolean matches(Class<?> clazz) {
        return clazz.equals(LocalDateTime.class);
    }

    @Override
    public LocalDateTime from(ResultSet resultSet, String column) throws SQLException {
        return (LocalDateTime) resultSet.getObject(column);
    }

    @Override
    public void to(PreparedStatement statement, int index, LocalDateTime value) throws SQLException {
        statement.setObject(index, value);
    }

    @Override
    public String getSqlType(ColumnAttribute attribute) {
        return "DATETIME ";
    }
}
