package de.paul2708.worm.database.sql.helper;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.datatypes.ColumnsRegistry;
import de.paul2708.worm.util.Reflections;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CollectionSupportTable {

    private final AttributeResolver entityResolver;
    private final ColumnAttribute collectionAttribute;

    private final DataSource dataSource;
    private final ColumnsRegistry registry;

    private final String tableName;

    public CollectionSupportTable(AttributeResolver entityResolver, ColumnAttribute collectionAttribute, DataSource dataSource, ColumnsRegistry registry) {
        this.entityResolver = entityResolver;
        this.collectionAttribute = collectionAttribute;

        this.dataSource = dataSource;
        this.registry = registry;

        this.tableName = entityResolver.getTable() + "_" + collectionAttribute.columnName();
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
                    .formatted(tableName, toSqlType(entityResolver.getPrimaryKey()), toSqlType(elementType),
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
                    .formatted(tableName, toSqlType(entityResolver.getPrimaryKey()), toSqlType(elementType),
                            entityResolver.getTable(), entityResolver.getPrimaryKey().columnName());
            query(query);
        }
    }

    private String toSqlType(Class<?> clazz) {
        return registry.getDataType(clazz).getSqlType(null);
    }

    private String toSqlType(ColumnAttribute columnAttribute) {
        return registry.getDataType(columnAttribute.type()).getSqlType(columnAttribute);
    }

    private void query(String query) {
        System.out.println(query);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
