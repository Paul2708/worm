package de.paul2708.worm.database.sql.collections;

import de.paul2708.worm.attributes.AttributeInformation;
import de.paul2708.worm.database.sql.ColumnMapper;
import de.paul2708.worm.database.sql.context.SQLFunction;
import de.paul2708.worm.util.Reflections;

import java.util.List;
import java.util.SortedMap;

public interface CollectionProvider {

    SortedMap<String, String> getTableCreationColumns(AttributeInformation collectionAttribute, ColumnMapper mapper);

    default int numberOfParameters(AttributeInformation attributeInformation, ColumnMapper mapper) {
        return getTableCreationColumns(attributeInformation, mapper).keySet().size();
    }

    int size(Object entity, AttributeInformation attributeInformation);

    List<List<Object>> getSqlValues(Object entity, AttributeInformation attributeInformation);

    SQLFunction<Object> getValueFromResultSet(AttributeInformation attributeInformation, ColumnMapper mapper);

    static CollectionProvider of(AttributeInformation attribute) {
        if (!attribute.isCollection()) {
            throw new IllegalArgumentException("Column attribute must be a collection.");
        }

        if (Reflections.isList(attribute.type())) {
            return new ListProvider();
        } else if (Reflections.isSet(attribute.type())) {
            return new SetProvider();
        } else if (Reflections.isMap(attribute.type())) {
            return new MapProvider();
        } else if (attribute.type().isArray()) {
            return new ArrayProvider();
        }

        throw new IllegalArgumentException("There is no collection provider handling the type %s"
                .formatted(attribute.type()));
    }
}
