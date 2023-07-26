package de.paul2708.worm.database.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.columns.StringColumnAttribute;
import de.paul2708.worm.database.Database;

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
        String sqlColumns = resolver.getColumns().stream()
                .map(column -> "%s %s".formatted(column.columnName(), toSqlType(column)))
                .collect(Collectors.joining(", "));

        String query = "CREATE TABLE IF NOT EXISTS %s (%s, PRIMARY KEY (%s))"
                .formatted(resolver.getTable(), sqlColumns, resolver.getPrimaryKey().columnName());

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.prepareStatement(query)) {
            stmt.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object save(AttributeResolver resolver, Object key, Object entity) {
        // TODO: Update on duplicated key

        String sqlColumns = resolver.getColumns().stream()
                .map(ColumnAttribute::columnName)
                .collect(Collectors.joining(", "));
        String sqlValues = resolver.getColumns().stream()
                .map(column -> "?")
                .collect(Collectors.joining(", "));

        String query = "INSERT INTO %s (%s) VALUES (%s)"
                .formatted(resolver.getTable(), sqlColumns, sqlValues);

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            int index = 1;
            for (ColumnAttribute column : resolver.getColumns()) {
                setValue(stmt, column.type(), index, resolver.getValueByColumn(entity, column.columnName()));
                index++;
            }

            stmt.execute();

            // TODO: Set key from database if key is null
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Object> findAll(AttributeResolver resolver) {
        String query = "SELECT * FROM %s".formatted(resolver.getTable());

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet resultSet = stmt.executeQuery();
            List<Object> result = new ArrayList<>();

            while (resultSet.next()) {
                Map<String, Object> fieldValues = new HashMap<>();
                for (ColumnAttribute column : resolver.getColumns()) {
                    fieldValues.put(column.fieldName(), getValue(resultSet, column.columnName(), column.type()));
                }

                Object instance = resolver.createInstance(fieldValues);
                result.add(instance);
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Object> findById(AttributeResolver resolver, Object key) {
        return Optional.empty();
    }

    @Override
    public void delete(AttributeResolver resolver, Object key) {

    }

    private Object getValue(ResultSet resultSet, String column, Class<?> expectedType) {
        try {
            if (expectedType.equals(String.class)) {
                return resultSet.getString(column);
            } else if (expectedType.equals(Integer.class) || expectedType.equals(int.class)) {
                return resultSet.getInt(column);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private void setValue(PreparedStatement statement, Class<?> type, int index, Object value) {
        try {
            if (type.equals(String.class)) {
                statement.setString(index, value.toString());
            } else if (type.equals(Integer.class) || type.equals(int.class)) {
                statement.setInt(index, (int) value);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String toSqlType(ColumnAttribute attribute) {
        Class<?> type = attribute.type();

        if (attribute instanceof StringColumnAttribute stringAttribute) {
            if (stringAttribute.hasMaximumLength()) {
                return "VARCHAR(%d)".formatted(stringAttribute.getMaxLength());
            } else {
                return "TEXT";
            }
        }

        if (type.equals(Integer.class) || type.equals(int.class)) {
            return "INT";
        }

        throw new IllegalArgumentException("Could not find a SQL type for %s".formatted(type.getName()));
    }
}