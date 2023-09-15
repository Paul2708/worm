package de.paul2708.worm.database.sql.datatypes.impl;

import de.paul2708.worm.attributes.AttributeInformation;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DoubleColumnDataType implements ColumnDataType<Double> {

    @Override
    public boolean matches(Class<?> clazz) {
        return clazz.equals(Double.class) || clazz.equals(double.class);
    }

    @Override
    public Double from(ResultSet resultSet, AttributeInformation attribute, String column) throws SQLException {
        return resultSet.getDouble(column);
    }

    @Override
    public void to(PreparedStatement statement, int index, AttributeInformation attribute, Double value) throws SQLException {
        statement.setDouble(index, value);
    }

    @Override
    public String getSqlType(AttributeInformation attribute) {
        return "DOUBLE";
    }
}
