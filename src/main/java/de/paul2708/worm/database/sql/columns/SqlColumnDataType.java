package de.paul2708.worm.database.sql.columns;

import de.paul2708.worm.columns.ColumnAttribute;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface SqlColumnDataType<T> extends ColumnDataType {

	T getValue(ResultSet resultSet, String column) throws SQLException;

	void setValue(PreparedStatement statement, int index, T value) throws SQLException;

	/**
	 * Convert the column attribute to a sql type.
	 *
	 * @param attribute column attribute
	 * @return sql type
	 */
	String toSqlType(ColumnAttribute attribute);
}
