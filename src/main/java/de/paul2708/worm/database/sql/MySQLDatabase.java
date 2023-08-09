package de.paul2708.worm.database.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.columns.datatypes.ColumnDataType;
import de.paul2708.worm.columns.datatypes.ColumnsRegistry;
import de.paul2708.worm.database.Database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String sqlColumns = resolver.getColumns().stream()
                .map(column -> "%s %s".formatted(column.columnName(), toSqlType(column)))
                .collect(Collectors.joining(", "));

        String query = "CREATE TABLE IF NOT EXISTS %s (%s, PRIMARY KEY (%s))"
                .formatted(resolver.getTable(), sqlColumns, resolver.getPrimaryKey().columnName());

        try (Connection conn = dataSource.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

	@Override
	public void registerColumnsRegistry(ColumnsRegistry registry) {
		if (registry == null) {
			throw new IllegalArgumentException("Registry that was provided is null");
		}
		this.columnsRegistry = registry;
		this.columnsRegistry.init();
	}

	@Override
	public void registerDataType(ColumnDataType<?> dataType) {
		if (dataType == null) {
			throw new IllegalArgumentException("Data type that was provided is null");
		}
		this.columnsRegistry.register(dataType);
	}

    @Override
    public Object save(AttributeResolver resolver, Object key, Object entity) {
        String sqlColumns = resolver.getColumns().stream()
                .map(ColumnAttribute::columnName)
                .collect(Collectors.joining(", "));
        String sqlValues = resolver.getColumns().stream()
                .map(column -> "?")
                .collect(Collectors.joining(", "));
        String sqlUpdate = resolver.getColumnsWithoutPrimaryKey().stream()
                .map(ColumnAttribute::columnName)
                .map("%s = ?"::formatted)
                .collect(Collectors.joining(", "));

        String query = "INSERT INTO %s (%s) VALUES (%s) ON DUPLICATE KEY UPDATE %s"
                .formatted(resolver.getTable(), sqlColumns, sqlValues, sqlUpdate);

        try (Connection conn = dataSource.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(query)) {
            int index = 1;
            for (ColumnAttribute column : resolver.getColumns()) {
                setValue(stmt, column.type(), index, resolver.getValueByColumn(entity, column.columnName()));
                index++;
            }
            for (ColumnAttribute column : resolver.getColumnsWithoutPrimaryKey()) {
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

        try (Connection conn = dataSource.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet resultSet = stmt.executeQuery();
            List<Object> result = new ArrayList<>();

            while (resultSet.next()) {
				Map<String, Object> fieldValues = getFieldValues(resolver, resultSet);

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
        String query = "SELECT * FROM %s WHERE %s = ?"
                .formatted(resolver.getTable(), resolver.getPrimaryKey().columnName());

        try (Connection conn = dataSource.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(query)) {
            setValue(stmt, resolver.getPrimaryKey().type(), 1, key);
            ResultSet resultSet = stmt.executeQuery();

            // TODO: Handle multiple responses, throw error
            if (resultSet.next()) {
				Map<String, Object> fieldValues = getFieldValues(resolver, resultSet);

				Object instance = resolver.createInstance(fieldValues);
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
            setValue(stmt, resolver.getPrimaryKey().type(), 1, resolver.getValueByColumn(entity, resolver.getPrimaryKey().columnName()));
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

	private Map<String, Object> getFieldValues(AttributeResolver resolver, ResultSet resultSet) {
		Map<String, Object> fieldValues = new HashMap<>();
		for (ColumnAttribute column : resolver.getColumns()) {
			fieldValues.put(column.fieldName(), getValue(resultSet, column.columnName(), column.type()));
		}
		return fieldValues;
	}

    private Object getValue(ResultSet resultSet, String column, Class<?> expectedType) {
		try {
			return columnsRegistry.getDataType(expectedType).from(resultSet, column);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setValue(PreparedStatement statement, Class<?> expectedType, int index, Object value) {
        try {
			columnsRegistry.getDataType(expectedType).unsafeTo(statement, index, value);
		} catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String toSqlType(ColumnAttribute attribute) {
        Class<?> type = attribute.type();
		return columnsRegistry.getDataType(type).getSqlType(attribute);
    }
}