package de.paul2708.worm.database.sql.columns;

import java.util.HashSet;
import java.util.Set;

public final class ColumnsRegistry {

	private ColumnsRegistry() {
	    throw new IllegalAccessError("Illegal access of ColumnsRegistry - No instantiation!");
	}

	private static final Set<ColumnDataType> DATA_TYPES = new HashSet<>();

	static {
		registerDataType(new UUIDSqlColumnDataType());
		registerDataType(new IntegerSqlColumnDataType());
		registerDataType(new StringSqlColumnDataType());
	}

	public static ColumnDataType getColumnDataType(Class<?> expectedType) {
		return DATA_TYPES.stream()
				.filter(x -> x.matches(expectedType))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Unsupported sql data type " + expectedType.getName()));
	}

	public static void registerDataType(ColumnDataType dataType) {
		DATA_TYPES.add(dataType);
	}
}
