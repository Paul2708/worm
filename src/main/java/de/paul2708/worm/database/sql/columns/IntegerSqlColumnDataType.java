package de.paul2708.worm.database.sql.columns;

import de.paul2708.worm.columns.ColumnAttribute;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class IntegerSqlColumnDataType implements SqlColumnDataType<Integer> {
	@Override
	public boolean matches(Class<?> expectedType) {
		return expectedType.equals(Integer.class) || expectedType.equals(int.class);
	}

	@Override
	public Integer getValue(ResultSet resultSet, String column) throws SQLException {
		return resultSet.getInt(column);
	}

	@Override
	public void setValue(PreparedStatement statement, int index, Integer value) throws SQLException {
		statement.setInt(index, value);
	}

	@Override
	public String toSqlType(ColumnAttribute attribute) {
		return "INT";
	}
}
