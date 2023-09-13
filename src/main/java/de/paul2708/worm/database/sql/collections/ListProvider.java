package de.paul2708.worm.database.sql.collections;

import de.paul2708.worm.attributes.AttributeInformation;
import de.paul2708.worm.database.sql.ColumnMapper;
import de.paul2708.worm.database.sql.context.SQLFunction;
import de.paul2708.worm.util.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ListProvider implements CollectionProvider {

    @Override
    public SortedMap<String, String> getTableCreationColumns(AttributeInformation collectionAttribute, ColumnMapper mapper) {
        SortedMap<String, String> map = new TreeMap<>();

        map.put("`index`", "INT");
        map.put("`value`", mapper.toSqlType(Reflections.getElementType(collectionAttribute.getField())));

        return map;
    }

    @Override
    public int size(Object entity, AttributeInformation attributeInformation) {
        return ((List<?>) attributeInformation.getValue(entity)).size();
    }

    @Override
    public List<List<Object>> getSqlValues(Object entity, AttributeInformation attributeInformation) {
        List<Object> list = (List<Object>) attributeInformation.getValue(entity);

        List<List<Object>> values = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            values.add(List.of(i, list.get(i)));
        }

        return values;
    }

    @Override
    public SQLFunction<Object> getValueFromResultSet(AttributeInformation attributeInformation, ColumnMapper mapper) {
        return resultSet -> {
            List<Object> list = new ArrayList<>();

            while (resultSet.next()) {
                int index = resultSet.getInt("index");
                Object value = mapper.getValue(resultSet, "value", Reflections.getElementType(attributeInformation.getField()));
                list.add(index, value);
            }

            return list;
        };
    }
}
