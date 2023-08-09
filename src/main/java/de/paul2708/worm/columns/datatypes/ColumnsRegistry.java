package de.paul2708.worm.columns.datatypes;

public interface ColumnsRegistry {

	static ColumnsRegistry create() {
		return new ColumnRegistryImpl();
	}

	void init();

	<T> void register(ColumnDataType<T> dataType);

	<T> ColumnDataType<T> getDataType(Class<T> clazz);
}
