package de.paul2708.worm.database.sql;

import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.datatypes.ColumnsRegistry;

public class ColumnMapper {

    private final ColumnsRegistry registry;

    public ColumnMapper(ColumnsRegistry registry) {
        this.registry = registry;
    }

    public String toSqlType(ColumnAttribute column) {
        Class<?> type = column.type();

        return registry.getDataType(type).getSqlType(column);
    }
}
