package de.paul2708.worm.database.sql.datatypes;

public interface ColumnsRegistry {

    static ColumnsRegistry create() {
        return new DefaultColumnRegistry();
    }

    void init();

    <T> void register(ColumnDataType<T> dataType);

    <T> ColumnDataType<T> getDataType(Class<T> clazz);
}
