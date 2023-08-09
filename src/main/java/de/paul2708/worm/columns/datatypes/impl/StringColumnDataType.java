package de.paul2708.worm.columns.datatypes.impl;

import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.columns.datatypes.ColumnDataType;
import de.paul2708.worm.columns.properties.LengthRestrictedProperty;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class StringColumnDataType implements ColumnDataType<String> {
	@Override
	public boolean matches(Class<?> clazz) {
		return clazz.equals(String.class);
	}

	@Override
	public String from(ResultSet resultSet, String column) throws SQLException {
		return resultSet.getString(column);
	}

	@Override
	public void to(PreparedStatement statement, int index, String value) throws SQLException {
		statement.setString(index, value);
	}

	@Override
	public String getSqlType(ColumnAttribute attribute) {
		if (attribute.hasProperty(LengthRestrictedProperty.class)) {
			int length = attribute.getProperty(LengthRestrictedProperty.class).length();

			return "VARCHAR(%d)".formatted(length);
		} else {
			return "TEXT";
		}
	}
}
