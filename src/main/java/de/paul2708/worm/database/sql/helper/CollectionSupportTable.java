package de.paul2708.worm.database.sql.helper;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.ColumnMapper;
import de.paul2708.worm.database.sql.context.ConnectionContext;
import de.paul2708.worm.database.sql.datatypes.ColumnsRegistry;
import de.paul2708.worm.util.Reflections;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionSupportTable {

    private final AttributeResolver entityResolver;
    private final ColumnAttribute collectionAttribute;

    private final DataSource dataSource;
    private final ColumnsRegistry registry;

    private final String tableName;

    private final ColumnMapper mapper;
    private final ConnectionContext context;

    public CollectionSupportTable(AttributeResolver entityResolver, ColumnAttribute collectionAttribute,
                                  DataSource dataSource, ColumnsRegistry registry, ColumnMapper mapper,
                                  ConnectionContext context) {
        this.entityResolver = entityResolver;
        this.collectionAttribute = collectionAttribute;

        this.dataSource = dataSource;
        this.registry = registry;

        this.tableName = entityResolver.getTable() + "_" + collectionAttribute.columnName();

        this.mapper = mapper;
        this.context = context;
    }

    public void create() {
        String query = null;
        if (Reflections.isList(collectionAttribute.type())) {
            Class<?> elementType = Reflections.getElementType(collectionAttribute.getField());

            query = ("CREATE TABLE IF NOT EXISTS %s ("
                    + "id INT NOT NULL AUTO_INCREMENT, "
                    + "parent_id %s, "
                    + "`index` INT, "
                    + "value %s, "
                    + "PRIMARY KEY (id), "
                    + "FOREIGN KEY (parent_id) REFERENCES %s(%s))")
                    .formatted(tableName, mapper.toSqlType(entityResolver.getPrimaryKey()), mapper.toSqlType(elementType),
                            entityResolver.getTable(), entityResolver.getPrimaryKey().columnName());
        } else if (Reflections.isSet(collectionAttribute.type())) {
            Class<?> elementType = Reflections.getElementType(collectionAttribute.getField());

            query = ("CREATE TABLE IF NOT EXISTS %s ("
                    + "id INT NOT NULL AUTO_INCREMENT, "
                    + "parent_id %s, "
                    + "value %s, "
                    + "PRIMARY KEY (id), "
                    + "FOREIGN KEY (parent_id) REFERENCES %s(%s))")
                    .formatted(tableName, mapper.toSqlType(entityResolver.getPrimaryKey()), mapper.toSqlType(elementType),
                            entityResolver.getTable(), entityResolver.getPrimaryKey().columnName());
        }

        if (query == null) {
            throw new RuntimeException("Failed to create collection table for %s".formatted(collectionAttribute));
        }

        context.query(query);
    }

    public void deleteExistingElements(Object entity) {
        if (collectionAttribute.isCollection()) {
            String query = "DELETE %s FROM %s WHERE parent_id = ?"
                    .formatted(tableName, tableName);

            context.query(query, statement -> {
                mapper.setParameterValue(entityResolver.getPrimaryKey(), entity, statement, 1);
            });
        }
    }

    public void insert(Object entity) {
        if (Reflections.isList(collectionAttribute.type())) {
            List<?> list = (List<?>) collectionAttribute.getValue(entity);
            if (list.isEmpty()) {
                return;
            }
            List<String> sqlValues2 = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                sqlValues2.add("(?, %d, ?)".formatted(i));
            }

            String query2 = "INSERT INTO %s (parent_id, `index`, value) VALUES %s"
                    .formatted(tableName,
                            String.join(", ", sqlValues2));

            context.query(query2, statement -> {
                int index = 1;
                for (int i = 0; i < list.size(); i++) {
                    mapper.setParameterValue(entityResolver.getPrimaryKey(), entity, statement, index);
                    index++;
                    mapper.setDirectParameterValue(Reflections.getElementType(collectionAttribute.getField()),
                            list.get(i), statement, index);
                    index++;
                }
            });
        } else if (Reflections.isSet(collectionAttribute.type())) {
            Set<?> set = (Set<?>) collectionAttribute.getValue(entity);
            if (set.isEmpty()) {
                return;
            }

            List<String> sqlValues2 = new ArrayList<>();
            for (Object ignored : set) {
                sqlValues2.add("(?, ?)");
            }

            String query2 = "INSERT INTO %s (parent_id, value) VALUES %s"
                    .formatted(tableName,
                            String.join(", ", sqlValues2));

            context.query(query2, statement -> {
                int index = 1;

                for (Object element : set) {
                    mapper.setParameterValue(entityResolver.getPrimaryKey(), entity, statement, index);
                    index++;
                    mapper.setDirectParameterValue(Reflections.getElementType(collectionAttribute.getField()),
                            element, statement, index);
                    index++;
                }
            });
        }
    }

    public Object get(Object entity) {
        String query = "SELECT * FROM " + tableName + " WHERE parent_id = ?";

        return context.query(query, statement -> {
            mapper.setParameterValue(entityResolver.getPrimaryKey(), entity, statement, 1);
        }, resultSet -> {
            if (Reflections.isList(collectionAttribute.type())) {
                List<Object> list = new ArrayList<>();

                while (resultSet.next()) {
                    int index = resultSet.getInt("index");
                    Object value = getValue(resultSet, "value", Reflections.getElementType(collectionAttribute.getField()));
                    list.add(index, value);
                }

                return list;
            } else if (Reflections.isSet(collectionAttribute.type())) {
                Set<Object> set = new HashSet<>();

                while (resultSet.next()) {
                    Object value = getValue(resultSet, "value", Reflections.getElementType(collectionAttribute.getField()));
                    set.add(value);
                }

                return set;
            }

            throw new RuntimeException("Failed to get collection attribute %s".formatted(collectionAttribute));
        });
    }

    private Object getValue(ResultSet resultSet, String column, Class<?> expectedType) {
        try {
            return registry.getDataType(expectedType).from(resultSet, null, column);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
