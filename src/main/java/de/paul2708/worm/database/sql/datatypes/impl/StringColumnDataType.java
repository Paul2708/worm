package de.paul2708.worm.database.sql.datatypes.impl;

import de.paul2708.worm.attributes.AttributeInformation;
import de.paul2708.worm.attributes.properties.LengthRestrictedProperty;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class StringColumnDataType implements ColumnDataType<String> {

    @Override
    public boolean matches(Class<?> clazz) {
        return clazz.equals(String.class);
    }

    @Override
    public String from(ResultSet resultSet, AttributeInformation attribute, String column) throws SQLException {
        return resultSet.getString(column);
    }

    @Override
    public void to(PreparedStatement statement, int index, AttributeInformation attribute, String value) throws SQLException {
        statement.setString(index, value);
    }

    @Override
    public String getSqlType(AttributeInformation attribute) {
        if (attribute == null) {
            return "TEXT";
        }

        if (attribute.hasProperty(LengthRestrictedProperty.class)) {
            int length = attribute.getProperty(LengthRestrictedProperty.class).length();

            return "VARCHAR(%d)".formatted(length);
        } else {
            return "TEXT";
        }
    }
}
