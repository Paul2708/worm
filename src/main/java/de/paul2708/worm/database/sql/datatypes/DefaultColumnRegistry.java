package de.paul2708.worm.database.sql.datatypes;

import de.paul2708.worm.database.sql.datatypes.impl.*;
import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

final class DefaultColumnRegistry implements ColumnsRegistry {

    private final Set<ColumnDataType<?>> dataTypes;

    DefaultColumnRegistry() {
        dataTypes = new CopyOnWriteArraySet<>();
    }

    @Override
    public void init() {
        dataTypes.clear(); // reset to prevent exception when reloading (See #register)

        register(new BooleanColumnDataType());
        register(new IntegerColumnDataType());
        register(new StringColumnDataType());
        register(new UUIDColumnDataType());
        register(new LocalDateTimeColumnDataType());
    }

    @Override
    public <T> void register(ColumnDataType<T> dataType) {
        if (dataTypes.contains(dataType)) {
            throw new IllegalArgumentException("Data type %s is already registered".formatted(dataType.getClass().getName()));
        }

        dataTypes.add(dataType);
    }

    @Override
    public <T> ColumnDataType<T> getDataType(Class<T> clazz) {
        ColumnDataType<?> dataType = getRegisteredDataType(clazz);

        if (dataType == null) {
            dataType = getDataTypeFromPrimaryKey(clazz);
        }

        if (dataType != null) {
            return (ColumnDataType<T>) dataType;
        }

        throw new IllegalArgumentException("Unsupported data type " + clazz.getName());
    }

    private ColumnDataType<?> getRegisteredDataType(Class<?> clazz) {
        for (ColumnDataType<?> dataType : dataTypes) {
            if (dataType.matches(clazz)) {
                return dataType;
            }
        }

        return null;
    }

    private ColumnDataType<?> getDataTypeFromPrimaryKey(Class<?> clazz) {
        ColumnAttribute primaryKey = new AttributeResolver(clazz).getPrimaryKey();
        if (primaryKey == null) {
            return null;
        }

        return getRegisteredDataType(primaryKey.type());
    }
}
