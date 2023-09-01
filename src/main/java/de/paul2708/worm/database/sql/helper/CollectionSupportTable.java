package de.paul2708.worm.database.sql.helper;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.ColumnMapper;
import de.paul2708.worm.database.sql.collections.CollectionProvider;
import de.paul2708.worm.database.sql.collections.ListProvider;
import de.paul2708.worm.database.sql.collections.SetProvider;
import de.paul2708.worm.database.sql.context.ConnectionContext;
import de.paul2708.worm.database.sql.datatypes.ColumnsRegistry;
import de.paul2708.worm.util.Reflections;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CollectionSupportTable {

    private final AttributeResolver entityResolver;
    private final ColumnAttribute collectionAttribute;

    private final String tableName;

    private final ColumnMapper mapper;
    private final ConnectionContext context;

    private final CollectionProvider collectionProvider;

    public CollectionSupportTable(AttributeResolver entityResolver, ColumnAttribute collectionAttribute,
                                  ColumnMapper mapper, ConnectionContext context) {
        this.entityResolver = entityResolver;
        this.collectionAttribute = collectionAttribute;

        this.tableName = entityResolver.getTable() + "_" + collectionAttribute.columnName();

        this.mapper = mapper;
        this.context = context;

        this.collectionProvider = Reflections.isList(collectionAttribute.type()) ?
                new ListProvider() : new SetProvider();
    }

    public void create() {
        SortedMap<String, String> columns = collectionProvider.getTableCreationColumns(collectionAttribute, mapper);
        String collectionColumns = columns.keySet().stream()
                .map(column -> column + " " + columns.get(column))
                .collect(Collectors.joining(", "));

        String query = ("CREATE TABLE IF NOT EXISTS %s ("
                + "id INT NOT NULL AUTO_INCREMENT, "
                + "parent_id %s, "
                + "%s, "
                + "PRIMARY KEY (id), "
                + "FOREIGN KEY (parent_id) REFERENCES %s(%s))")
                .formatted(tableName, mapper.toSqlType(entityResolver.getPrimaryKey()), collectionColumns,
                        entityResolver.getTable(), entityResolver.getPrimaryKey().columnName());

        context.query(query);
    }

    public void deleteExistingElements(Object entity) {
        String query = "DELETE %s FROM %s WHERE parent_id = ?"
                .formatted(tableName, tableName);

        context.query(query, statement -> {
            mapper.setParameterValue(entityResolver.getPrimaryKey(), entity, statement, 1);
        });
    }

    public void insert(Object entity) {
        if (collectionProvider.size(entity, collectionAttribute) == 0) {
            return;
        }

        String collectionColumns = String.join(", ", collectionProvider.getTableCreationColumns(collectionAttribute, mapper)
                .keySet());
        String collectionParameters = IntStream.range(0, collectionProvider.numberOfParameters(entity, collectionAttribute) + 1)
                .mapToObj(i -> "?")
                .collect(Collectors.joining(", "));
        String allCollectionParameters = IntStream.range(0, collectionProvider.size(entity, collectionAttribute))
                .mapToObj(i -> "(%s)".formatted(collectionParameters))
                .collect(Collectors.joining(", "));

        String query = "INSERT INTO %s (parent_id, %s) VALUES %s"
                .formatted(tableName, collectionColumns, allCollectionParameters);

        context.query(query, statement -> {
            int index = 1;

            for (List<Object> sqlValues : collectionProvider.getSqlValues(entity, collectionAttribute)) {
                mapper.setParameterValue(entityResolver.getPrimaryKey(), entity, statement, index);
                index++;

                for (Object sqlValue : sqlValues) {
                    mapper.setDirectParameterValue(Reflections.getElementType(collectionAttribute.getField()),
                            sqlValue, statement, index);
                    index++;
                }
            }
        });
    }

    public Object get(Object entity) {
        String query = "SELECT * FROM " + tableName + " WHERE parent_id = ?";

        return context.query(query, statement -> {
            mapper.setParameterValue(entityResolver.getPrimaryKey(), entity, statement, 1);
        }, collectionProvider.getValueFromResultSet(collectionAttribute, mapper));
    }
}
