package de.paul2708.worm.database.sql.collections;

import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.ColumnMapper;
import de.paul2708.worm.database.sql.context.SQLFunction;

import java.util.List;
import java.util.SortedMap;

public interface CollectionProvider {

    SortedMap<String, String> getTableCreationColumns(ColumnAttribute collectionAttribute, ColumnMapper mapper);

    int numberOfParameters(Object entity, ColumnAttribute columnAttribute);

    int size(Object entity, ColumnAttribute columnAttribute);

    List<List<Object>> getSqlValues(Object entity, ColumnAttribute columnAttribute);

    SQLFunction<Object> getValueFromResultSet(ColumnAttribute columnAttribute, ColumnMapper mapper);
}
