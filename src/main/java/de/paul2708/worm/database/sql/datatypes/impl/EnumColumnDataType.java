package de.paul2708.worm.database.sql.datatypes.impl;

import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class EnumColumnDataType implements ColumnDataType<Enum<?>> {

    @Override
    public boolean matches(Class<?> clazz) {
        return clazz.isEnum();
    }

    @Override
    public Enum<?> from(ResultSet resultSet, ColumnAttribute attribute, String column) throws SQLException {
        return Enum.valueOf((Class<? extends Enum>) attribute.type(), resultSet.getString(column));
    }

    @Override
    public void to(PreparedStatement statement, int index, ColumnAttribute attribute, Enum<?> value) throws SQLException {
        statement.setString(index, value.name());
    }

    @Override
    public String getSqlType(ColumnAttribute attribute) {
        Object[] enumConstants = attribute.type().getEnumConstants();

        String enumValues = Arrays.stream(enumConstants)
                .map(obj -> "'%s'".formatted(obj.toString()))
                .collect(Collectors.joining(", "));

        return "ENUM(%s)".formatted(enumValues);
    }
}
