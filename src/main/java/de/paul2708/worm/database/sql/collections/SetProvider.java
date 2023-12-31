package de.paul2708.worm.database.sql.collections;

import de.paul2708.worm.attributes.AttributeInformation;
import de.paul2708.worm.database.sql.ColumnMapper;
import de.paul2708.worm.database.sql.context.SQLFunction;
import de.paul2708.worm.util.Reflections;

import java.util.*;

public class SetProvider implements CollectionProvider {

    @Override
    public SortedMap<String, String> getTableCreationColumns(AttributeInformation collectionAttribute, ColumnMapper mapper) {
        SortedMap<String, String> map = new TreeMap<>();

        map.put("`value`", mapper.toSqlType(Reflections.getElementType(collectionAttribute.getField())));

        return map;
    }

    @Override
    public int size(Object entity, AttributeInformation attributeInformation) {
        return ((Set<?>) attributeInformation.getValue(entity)).size();
    }

    @Override
    public List<List<Object>> getSqlValues(Object entity, AttributeInformation attributeInformation) {
        Set<Object> set = (Set<Object>) attributeInformation.getValue(entity);

        List<List<Object>> values = new ArrayList<>();

        for (Object element : set) {
            values.add(List.of(element));
        }

        return values;
    }

    @Override
    public SQLFunction<Object> getValueFromResultSet(AttributeInformation attributeInformation, ColumnMapper mapper) {
        return resultSet -> {
            Set<Object> set = new HashSet<>();

            while (resultSet.next()) {
                Object value = mapper.getValue(resultSet, "value", Reflections.getElementType(attributeInformation.getField()));
                set.add(value);
            }

            return set;
        };
    }
}
