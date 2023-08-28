package de.paul2708.worm.database.sql.helper;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.columns.properties.ForeignKeyProperty;
import de.paul2708.worm.database.sql.ColumnMapper;
import de.paul2708.worm.database.sql.datatypes.ColumnsRegistry;
import de.paul2708.worm.util.Reflections;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CollectionSupportTable {

    private final AttributeResolver entityResolver;
    private final ColumnAttribute collectionAttribute;

    private final DataSource dataSource;
    private final ColumnsRegistry registry;

    private final String tableName;

    private final ColumnMapper mapper;

    public CollectionSupportTable(AttributeResolver entityResolver, ColumnAttribute collectionAttribute, DataSource dataSource, ColumnsRegistry registry, ColumnMapper mapper) {
        this.entityResolver = entityResolver;
        this.collectionAttribute = collectionAttribute;

        this.dataSource = dataSource;
        this.registry = registry;

        this.tableName = entityResolver.getTable() + "_" + collectionAttribute.columnName();

        this.mapper = mapper;
    }

    public void create() {
        if (Reflections.isList(collectionAttribute.type())) {
            Class<?> elementType = Reflections.getElementType(collectionAttribute.getField());

            String query = ("CREATE TABLE IF NOT EXISTS %s ("
                    + "id INT NOT NULL AUTO_INCREMENT, "
                    + "parent_id %s, "
                    + "`index` INT, "
                    + "value %s, "
                    + "PRIMARY KEY (id), "
                    + "FOREIGN KEY (parent_id) REFERENCES %s(%s))")
                    .formatted(tableName, mapper.toSqlType(entityResolver.getPrimaryKey()), mapper.toSqlType(elementType),
                            entityResolver.getTable(), entityResolver.getPrimaryKey().columnName());
            query(query);
        } else if (Reflections.isSet(collectionAttribute.type())) {
            Class<?> elementType = Reflections.getElementType(collectionAttribute.getField());

            String query = ("CREATE TABLE IF NOT EXISTS %s ("
                    + "id INT NOT NULL AUTO_INCREMENT, "
                    + "parent_id %s, "
                    + "value %s, "
                    + "PRIMARY KEY (id), "
                    + "FOREIGN KEY (parent_id) REFERENCES %s(%s))")
                    .formatted(tableName, mapper.toSqlType(entityResolver.getPrimaryKey()), mapper.toSqlType(elementType),
                            entityResolver.getTable(), entityResolver.getPrimaryKey().columnName());
            query(query);
        }
    }

    public void deleteExistingElements(Object entity) {
        if (collectionAttribute.isCollection()) {
            String query = "DELETE %s FROM %s WHERE parent_id = ?"
                    .formatted(tableName, tableName);

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                mapper.setParameterValue(entityResolver.getPrimaryKey(), entity, stmt, 1);
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query2)) {
                int index = 1;
                for (int i = 0; i < list.size(); i++) {
                    mapper.setParameterValue(entityResolver.getPrimaryKey(), entity, stmt, index);
                    index++;
                    mapper.setDirectParameterValue(Reflections.getElementType(collectionAttribute.getField()),
                            list.get(i), stmt, index);
                    index++;
                }

                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query2)) {
                int index = 1;

                for (Object element : set) {
                    mapper.setParameterValue(entityResolver.getPrimaryKey(), entity, stmt, index);
                    index++;
                    mapper.setDirectParameterValue(Reflections.getElementType(collectionAttribute.getField()),
                            element, stmt, index);
                    index++;
                }

                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Object get(Object entity) {
        String query = "SELECT * FROM " + tableName + " WHERE parent_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            mapper.setParameterValue(entityResolver.getPrimaryKey(), entity, stmt, 1);

            ResultSet resultSet = stmt.executeQuery();

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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private Object getValue(ResultSet resultSet, String column, Class<?> expectedType) {
        try {
            return registry.getDataType(expectedType).from(resultSet, null, column);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void query(String query) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
