package de.paul2708.worm.database.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.columns.CreatedAt;
import de.paul2708.worm.columns.UpdatedAt;
import de.paul2708.worm.columns.properties.ForeignKeyProperty;
import de.paul2708.worm.database.Database;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;
import de.paul2708.worm.database.sql.datatypes.ColumnsRegistry;
import de.paul2708.worm.database.sql.helper.EntityCreator;
import de.paul2708.worm.util.Reflections;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MySQLDatabase implements Database {

    private final String hostname;
    private final int port;
    private final String database;

    private final String username;
    private final String password;

    private DataSource dataSource;
    private ColumnsRegistry columnsRegistry;

    public MySQLDatabase(String hostname, int port, String database, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    @Override
    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (this.columnsRegistry == null) {
            registerColumnsRegistry(ColumnsRegistry.create());
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://%s:%d/%s".formatted(hostname, port, database));
        config.setUsername(username);
        config.setPassword(password);

        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);

        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public void prepare(AttributeResolver resolver) {
        // Create collection tables
        for (ColumnAttribute column : resolver.getColumns()) {
            if (Reflections.isList(column.type())) {
                Class<?> elementType = Reflections.getElementType(column.getField());

                String query = "CREATE TABLE IF NOT EXISTS %s (id INT NOT NULL AUTO_INCREMENT, `index` INT, value %s, PRIMARY KEY (id))"
                        .formatted(resolver.getTable() + "_" + column.columnName(), toSqlType(elementType));
                query(query);
            } else if (Reflections.isSet(column.type())) {
                Class<?> elementType = Reflections.getElementType(column.getField());

                String query = "CREATE TABLE IF NOT EXISTS %s (id int NOT NULL AUTO_INCREMENT, value %s, PRIMARY KEY (id))"
                        .formatted(resolver.getTable() + "_" + column.columnName(), toSqlType(elementType));
                query(query);
            }
        }

        // Create entity table
        String sqlColumns = resolver.getColumns().stream()
                .map(column -> "%s %s".formatted(column.columnName(), toSqlType(column)))
                .collect(Collectors.joining(", "));

        String foreignKeyReferences = resolver.getForeignKeys().stream()
                .map(column -> {
                    String table = column.getProperty(ForeignKeyProperty.class).getForeignTable();
                    String primaryKey = column.getProperty(ForeignKeyProperty.class).getForeignPrimaryKey().columnName();

                    return "FOREIGN KEY (%s) REFERENCES %s(%s)"
                            .formatted(column.columnName(), table, primaryKey);
                })
                .collect(Collectors.joining(", "));

        String query = "CREATE TABLE IF NOT EXISTS %s (%s, PRIMARY KEY (%s)"
                .formatted(resolver.getTable(), sqlColumns, resolver.getPrimaryKey().columnName());

        if (!resolver.getForeignKeys().isEmpty()) {
            query += ", %s".formatted(foreignKeyReferences) + ")";
        } else {
            query += ")";
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerColumnsRegistry(ColumnsRegistry registry) {
        if (registry == null) {
            throw new IllegalArgumentException("Registry that was provided is null");
        }
        this.columnsRegistry = registry;
        this.columnsRegistry.init();
    }

    public void registerDataType(ColumnDataType<?> dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Data type that was provided is null");
        }
        this.columnsRegistry.register(dataType);
    }

    @Override
    public Object save(AttributeResolver resolver, Object entity) {
        String sqlColumns = resolver.getColumns().stream()
                .filter(column -> !column.hasAnnotation(CreatedAt.class) && !column.hasAnnotation(UpdatedAt.class))
                .map(ColumnAttribute::columnName)
                .collect(Collectors.joining(", "));
        String sqlValues = resolver.getColumns().stream()
                .filter(column -> !column.hasAnnotation(CreatedAt.class) && !column.hasAnnotation(UpdatedAt.class))
                .map(column -> "?")
                .collect(Collectors.joining(", "));
        String sqlUpdate = resolver.getColumnsWithoutPrimaryKey().stream()
                .filter(column -> !column.hasAnnotation(CreatedAt.class) && !column.hasAnnotation(UpdatedAt.class))
                .map(ColumnAttribute::columnName)
                .map("%s = ?"::formatted)
                .collect(Collectors.joining(", "));

        for (ColumnAttribute column : resolver.getColumns()) {
            if (column.hasAnnotation(UpdatedAt.class)) {
                sqlUpdate += ", " + column.columnName() + " = CURRENT_TIMESTAMP(6)";
            }
        }

        String query = "INSERT INTO %s (%s) VALUES (%s) ON DUPLICATE KEY UPDATE %s"
                .formatted(resolver.getTable(), sqlColumns, sqlValues, sqlUpdate);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int index = 1;
            for (ColumnAttribute column : resolver.getColumns()) {
                if (column.hasAnnotation(CreatedAt.class) || column.hasAnnotation(UpdatedAt.class)) {
                    continue;
                }

                setValue(stmt, index, column, entity);
                index++;
            }
            for (ColumnAttribute column : resolver.getColumnsWithoutPrimaryKey()) {
                if (column.hasAnnotation(CreatedAt.class) || column.hasAnnotation(UpdatedAt.class)) {
                    continue;
                }

                setValue(stmt, index, column, entity);
                index++;
            }

            stmt.execute();

            // Fetch default column values
            List<ColumnAttribute> timestampColumns = resolver.getColumns().stream()
                    .filter(column -> column.hasAnnotation(CreatedAt.class) || column.hasAnnotation(UpdatedAt.class))
                    .sorted()
                    .toList();

            if (timestampColumns.isEmpty()) {
                return entity;
            }

            String timestampQuery = "SELECT %s FROM %s WHERE %s = ?"
                    .formatted(timestampColumns.stream().map(ColumnAttribute::columnName).collect(Collectors.joining(", ")),
                            resolver.getTable(),
                            resolver.getPrimaryKey().columnName());

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(timestampQuery)) {
                setValue(statement, resolver.getPrimaryKey().type(), 1, resolver.getPrimaryKey(),
                        resolver.getPrimaryKey().getValue(entity));

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    for (ColumnAttribute column : timestampColumns) {
                        Object timestamp = columnsRegistry.getDataType(column.type()).from(resultSet, column,
                                column.columnName());
                        column.setValue(entity, timestamp);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Object> findAll(AttributeResolver resolver) {
        // Build query
        String query = "SELECT * FROM %s%s"
                .formatted(resolver.getFormattedTableNames(),
                        resolver.getForeignKeys().isEmpty() ? "" : " WHERE " + buildConditions(resolver));

        // Query database
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet resultSet = stmt.executeQuery();

            List<Object> result = new ArrayList<>();

            while (resultSet.next()) {
                Object instance = EntityCreator.fromColumns(resolver.getTargetClass(), columnsRegistry, resultSet);
                result.add(instance);
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Object> findById(AttributeResolver resolver, Object key) {
        // Build query
        String query = "SELECT * FROM %s WHERE %s = ?%s"
                .formatted(resolver.getFormattedTableNames(), resolver.getPrimaryKey().getFullColumnName(),
                        resolver.getForeignKeys().isEmpty() ? "" : " AND " + buildConditions(resolver));

        // Query database
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            setValue(stmt, resolver.getPrimaryKey().type(), 1, resolver.getPrimaryKey(), key);

            ResultSet resultSet = stmt.executeQuery();

            // TODO: Handle multiple responses, throw error
            if (resultSet.next()) {
                Object instance = EntityCreator.fromColumns(resolver.getTargetClass(), columnsRegistry, resultSet);
                return Optional.of(instance);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(AttributeResolver resolver, Object entity) {
        String query = "DELETE FROM %s WHERE %s = ?"
                .formatted(resolver.getTable(), resolver.getPrimaryKey().columnName());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            setValue(stmt, resolver.getPrimaryKey().type(), 1, resolver.getPrimaryKey(),
                    resolver.getValueByColumn(entity, resolver.getPrimaryKey().columnName()));
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildConditions(AttributeResolver resolver) {
        return resolver.getForeignKeys().stream()
                .map(column -> {
                    String fullColumnName = column.getProperty(ForeignKeyProperty.class).getForeignPrimaryKey()
                            .getFullColumnName();
                    return column.getFullColumnName() + " = " + fullColumnName;
                })
                .collect(Collectors.joining(" AND "));
    }

    private void setValue(PreparedStatement statement, Class<?> expectedType, int index, ColumnAttribute attribute, Object value) {
        try {
            columnsRegistry.getDataType(expectedType).unsafeTo(statement, index, attribute, value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setValue(PreparedStatement statement, int index, ColumnAttribute column, Object entity) {
        if (column.isForeignKey()) {
            ColumnAttribute foreignPrimaryKey = column.getProperty(ForeignKeyProperty.class).getForeignPrimaryKey();
            Object value = foreignPrimaryKey.getValue(column.getValue(entity));

            setValue(statement, value.getClass(), index, column, value);
        } else {
            setValue(statement, column.type(), index, column, column.getValue(entity));
        }
    }

    private String toSqlType(ColumnAttribute attribute) {
        Class<?> type = attribute.type();
        return columnsRegistry.getDataType(type).getSqlType(attribute);
    }

    private String toSqlType(Class<?> type) {
        return columnsRegistry.getDataType(type).getSqlType(null);
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