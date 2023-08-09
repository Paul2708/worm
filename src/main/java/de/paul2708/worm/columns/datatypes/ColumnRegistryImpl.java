package de.paul2708.worm.columns.datatypes;

import de.paul2708.worm.columns.datatypes.impl.IntegerColumnDataType;
import de.paul2708.worm.columns.datatypes.impl.StringColumnDataType;
import de.paul2708.worm.columns.datatypes.impl.UUIDColumnDataType;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

final class ColumnRegistryImpl implements ColumnsRegistry {

	private final Set<ColumnDataType<?>> dataTypes;

	ColumnRegistryImpl() {
		dataTypes = new CopyOnWriteArraySet<>();
	}

	@Override
	public void init() {
		dataTypes.clear(); // reset to prevent exception when reloading (See #register)

		register(new IntegerColumnDataType());
		register(new StringColumnDataType());
		register(new UUIDColumnDataType());
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
		return dataTypes.stream()
				.map(x -> (ColumnDataType<T>) x)
				.filter(x -> x.matches(clazz))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Unsupported data type " + clazz.getName()));
	}
}
