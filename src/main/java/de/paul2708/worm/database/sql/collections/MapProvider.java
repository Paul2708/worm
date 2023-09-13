package de.paul2708.worm.database.sql.collections;

import de.paul2708.worm.attributes.AttributeInformation;
import de.paul2708.worm.database.sql.ColumnMapper;
import de.paul2708.worm.database.sql.context.SQLFunction;
import de.paul2708.worm.util.Reflections;

import java.util.*;

public class MapProvider implements CollectionProvider {

    @Override
    public SortedMap<String, String> getTableCreationColumns(AttributeInformation collectionAttribute, ColumnMapper mapper) {
        List<Class<?>> elementTypes = Reflections.getElementTypes(collectionAttribute.getField());
        Class<?> keyClass = elementTypes.get(0);
        Class<?> valueClass = elementTypes.get(1);

        SortedMap<String, String> map = new TreeMap<>();

        map.put("`key`", mapper.toSqlType(keyClass));
        map.put("`value`", mapper.toSqlType(valueClass));

        return map;
    }

    @Override
    public int size(Object entity, AttributeInformation attributeInformation) {
        return ((Map<?, ?>) attributeInformation.getValue(entity)).size();
    }

    @Override
    public List<List<Object>> getSqlValues(Object entity, AttributeInformation attributeInformation) {
        Map<Object, Object> map = (Map<Object, Object>) attributeInformation.getValue(entity);

        List<List<Object>> values = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            values.add(List.of(entry.getKey(), entry.getValue()));
        }

        return values;
    }

    @Override
    public SQLFunction<Object> getValueFromResultSet(AttributeInformation attributeInformation, ColumnMapper mapper) {
        return resultSet -> {
            Map<Object, Object> map = new HashMap<>();

            List<Class<?>> elementTypes = Reflections.getElementTypes(attributeInformation.getField());
            Class<?> keyClass = elementTypes.get(0);
            Class<?> valueClass = elementTypes.get(1);

            while (resultSet.next()) {
                Object key = mapper.getValue(resultSet, "key", keyClass);
                Object value = mapper.getValue(resultSet, "value", valueClass);

                map.put(key, value);
            }

            return map;
        };
    }
}
