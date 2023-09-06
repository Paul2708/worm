package de.paul2708.worm.database.sql.collections;

import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.ColumnMapper;
import de.paul2708.worm.database.sql.context.SQLFunction;
import de.paul2708.worm.util.Reflections;

import java.util.List;
import java.util.SortedMap;
import java.util.stream.LongStream;

public interface CollectionProvider {

    SortedMap<String, String> getTableCreationColumns(ColumnAttribute collectionAttribute, ColumnMapper mapper);

    default int numberOfParameters(ColumnAttribute columnAttribute, ColumnMapper mapper) {
        return getTableCreationColumns(columnAttribute, mapper).keySet().size();
    }

    int size(Object entity, ColumnAttribute columnAttribute);

    List<List<Object>> getSqlValues(Object entity, ColumnAttribute columnAttribute);

    SQLFunction<Object> getValueFromResultSet(ColumnAttribute columnAttribute, ColumnMapper mapper);

    static CollectionProvider of(ColumnAttribute attribute) {
        if (!attribute.isCollection()) {
            throw new IllegalArgumentException("Column attribute must be a collection.");
        }

        if (Reflections.isList(attribute.type())) {
            return new ListProvider();
        } else if (Reflections.isSet(attribute.type())) {
            return new SetProvider();
        }

        throw new IllegalArgumentException("There is no collection provider handling the type %s"
                .formatted(attribute.type()));
    }
}
