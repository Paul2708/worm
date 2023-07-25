package de.paul2708.worm.database.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.columns.StringColumnAttribute;
import de.paul2708.worm.database.Database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

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
    public void prepare(Class<?> entityClass) {
        AttributeResolver resolver = new AttributeResolver(entityClass);

        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS %s (".formatted(resolver.getTable()));

        query.append(resolver.getPrimaryKey().columnName())
                .append(" ")
                .append(toSqlType(resolver.getPrimaryKey()))
                .append(", ");

        for (ColumnAttribute column : resolver.getColumnsWithoutPrimaryKey()) {
            query.append(column.columnName())
                    .append(" ")
                    .append(toSqlType(column))
                    .append(", ");
        }

        query.append("PRIMARY KEY (")
                .append(resolver.getPrimaryKey().columnName())
                .append("));");

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.prepareStatement(query.toString())) {
            stmt.execute(query.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object save(Object key, Object entity) {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("SELECT 1");

            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Object> findAll() {
        return null;
    }

    @Override
    public Optional<Object> findById(Object key) {
        return Optional.empty();
    }

    @Override
    public void delete(Object key) {

    }

    private String toSqlType(ColumnAttribute attribute) {
        Class<?> type = attribute.type();

        if (attribute instanceof StringColumnAttribute stringAttribute) {
            if (stringAttribute.hasMaximumLength()) {
                return "VARCHAR(" + stringAttribute.getMaxLength() + ")";
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