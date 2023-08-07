package de.paul2708.worm.database.sql.columns;

public interface ColumnDataType {

	boolean matches(Class<?> expectedType);
}
