package de.paul2708.worm.database.sql.columns;

import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.columns.StringColumnAttribute;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class StringSqlColumnDataType implements SqlColumnDataType<String> {
	@Override
	public boolean matches(Class<?> expectedType) {
		return expectedType.equals(String.class);
	}

	@Override
	public String getValue(ResultSet resultSet, String column) throws SQLException {
		return resultSet.getString(column);
	}

	@Override
	public void setValue(PreparedStatement statement, int index, String value) throws SQLException {
		statement.setString(index, value);
	}

	@Override
	public String toSqlType(ColumnAttribute attribute) {
		if (!(attribute instanceof StringColumnAttribute stringAttribute)) {
			throw new IllegalArgumentException("Attribute is not a string attribute");
		}

		if (stringAttribute.hasMaximumLength()) {
			return "VARCHAR(%d)".formatted(stringAttribute.getMaxLength());
		} else {
			return "TEXT";
		}
	}
}
