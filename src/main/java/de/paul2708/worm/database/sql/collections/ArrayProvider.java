package de.paul2708.worm.database.sql.collections;

import de.paul2708.worm.attributes.AttributeInformation;
import de.paul2708.worm.database.sql.ColumnMapper;
import de.paul2708.worm.database.sql.context.SQLFunction;
import de.paul2708.worm.util.Reflections;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ArrayProvider implements CollectionProvider {

    @Override
    public SortedMap<String, String> getTableCreationColumns(AttributeInformation collectionAttribute, ColumnMapper mapper) {
        SortedMap<String, String> map = new TreeMap<>();

        map.put("`index`", "INT");
        map.put("`value`", mapper.toSqlType(Reflections.getElementTypeFromArray(collectionAttribute.getField())));

        return map;
    }

    @Override
    public int size(Object entity, AttributeInformation attributeInformation) {
        return Array.getLength(attributeInformation.getValue(entity));
    }

    @Override
    public List<List<Object>> getSqlValues(Object entity, AttributeInformation attributeInformation) {
        Object array = attributeInformation.getValue(entity);

        List<List<Object>> values = new ArrayList<>();

        for (int i = 0; i < Array.getLength(array); i++) {
            values.add(List.of(i, Array.get(array, i)));
        }

        return values;
    }

    @Override
    public SQLFunction<Object> getValueFromResultSet(AttributeInformation attributeInformation, ColumnMapper mapper) {
        return resultSet -> {
            List<Object> list = new ArrayList<>();

            while (resultSet.next()) {
                int index = resultSet.getInt("index");
                Object value = mapper.getValue(resultSet, "value",
                        Reflections.getElementTypeFromArray(attributeInformation.getField()));
                list.add(index, value);
            }

            // Create new array
            Object array = Array.newInstance(Reflections.getElementTypeFromArray(attributeInformation.getField()),
                    list.size());
            for (int i = 0; i < list.size(); i++) {
                Array.set(array, i, list.get(i));
            }

            return array;
        };
    }
}
