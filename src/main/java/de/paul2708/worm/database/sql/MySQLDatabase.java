package de.paul2708.worm.database.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.columns.CreatedAt;
import de.paul2708.worm.columns.UpdatedAt;
import de.paul2708.worm.columns.properties.ForeignKeyProperty;
import de.paul2708.worm.database.Database;
import de.paul2708.worm.database.sql.context.ConnectionContext;
import de.paul2708.worm.database.sql.context.SQLFunction;
import de.paul2708.worm.database.sql.datatypes.ColumnsRegistry;
import de.paul2708.worm.database.sql.helper.CollectionSupportTable;
import de.paul2708.worm.database.sql.helper.EntityCreator;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

public class MySQLDatabase implements Database {

    private final String hostname;
    private final int port;
    private final String database;

    private final String username;
    private final String password;

    private ColumnMapper mapper;

    private ConnectionContext context;

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

        ColumnsRegistry columnsRegistry = ColumnsRegistry.create();
        columnsRegistry.init();

        this.mapper = new ColumnMapper(columnsRegistry);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://%s:%d/%s".formatted(hostname, port, database));
        config.setUsername(username);
        config.setPassword(password);

        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);

        DataSource dataSource = new HikariDataSource(config);
        this.context = new ConnectionContext(dataSource);
    }

    @Override
    public void prepare(AttributeResolver resolver) {
        // Create entity table
        String sqlColumns = resolver.getColumns().stream()
                .filter(column -> !column.isCollection())
                .map(column -> "%s %s".formatted(column.columnName(), mapper.toSqlType(column)))
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

        context.query(query);

        // Create collection tables
        for (ColumnAttribute column : resolver.getColumns()) {
            if (column.isCollection()) {
                new CollectionSupportTable(resolver, column, mapper, context).create();
            }
        }
    }

    @Override
    public Object save(AttributeResolver resolver, Object entity) {
        // Save entity attributes
        String sqlColumns = resolver.getColumns().stream()
                .filter(column -> !column.hasAnnotation(CreatedAt.class) && !column.hasAnnotation(UpdatedAt.class) && !column.isCollection())
                .map(ColumnAttribute::columnName)
                .collect(Collectors.joining(", "));
        String sqlValues = resolver.getColumns().stream()
                .filter(column -> !column.hasAnnotation(CreatedAt.class) && !column.hasAnnotation(UpdatedAt.class)  && !column.isCollection())
                .map(column -> "?")
                .collect(Collectors.joining(", "));
        String sqlUpdate = resolver.getColumnsWithoutPrimaryKey().stream()
                .filter(column -> !column.hasAnnotation(CreatedAt.class) && !column.hasAnnotation(UpdatedAt.class)  && !column.isCollection())
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

        context.query(query, statement -> {
            int index = 1;
            for (ColumnAttribute column : resolver.getColumns()) {
                if (column.isAutoTimestamp() || column.isCollection()) {
                    continue;
                }

                mapper.setParameterValue(column, entity, statement, index);
                index++;
            }
            for (ColumnAttribute column : resolver.getColumnsWithoutPrimaryKey()) {
                if (column.isAutoTimestamp() || column.isCollection()) {
                    continue;
                }

                mapper.setParameterValue(column, entity, statement, index);
                index++;
            }
        });

        // Fetch default column values
        List<ColumnAttribute> timestampColumns = resolver.getColumns().stream()
                .filter(ColumnAttribute::isAutoTimestamp)
                .sorted()
                .toList();

        if (!timestampColumns.isEmpty()) {
            String timestampQuery = "SELECT %s FROM %s WHERE %s = ?"
                    .formatted(timestampColumns.stream().map(ColumnAttribute::columnName).collect(Collectors.joining(", ")),
                            resolver.getTable(),
                            resolver.getPrimaryKey().columnName());

            context.query(timestampQuery, statement -> {
                mapper.setParameterValue(resolver.getPrimaryKey(), entity, statement, 1);
            }, resultSet -> {
                if (resultSet.next()) {
                    for (ColumnAttribute column : timestampColumns) {
                        Object timestamp = mapper.getValue(resultSet, column);
                        column.setValue(entity, timestamp);
                    }
                }
            });
        }

        // Save collections
        for (ColumnAttribute column : resolver.getColumns()) {
            if (!column.isCollection()) {
                continue;
            }

            CollectionSupportTable supportTable = new CollectionSupportTable(resolver, column, mapper, context);
            supportTable.deleteExistingElements(entity);
            supportTable.insert(entity);
        }



        return entity;
    }

    @Override
    public Collection<Object> findAll(AttributeResolver resolver) {
        // Build query
        String query = "SELECT * FROM %s%s"
                .formatted(resolver.getFormattedTableNames(),
                        resolver.getForeignKeys().isEmpty() ? "" : " WHERE " + buildConditions(resolver));

        // Query database
        return context.query(query, (SQLFunction<Collection<Object>>) resultSet -> {
            List<Object> result = new ArrayList<>();

            while (resultSet.next()) {
                Object instance = EntityCreator.fromColumns(resolver.getTargetClass(), resultSet, mapper, context);
                result.add(instance);
            }

            return result;
        });
    }

    @Override
    public Optional<Object> findById(AttributeResolver resolver, Object key) {
        // Build query
        String query = "SELECT * FROM %s WHERE %s = ?%s"
                .formatted(resolver.getFormattedTableNames(),
                        resolver.getPrimaryKey().getFullColumnName(),
                        resolver.getForeignKeys().isEmpty() ? "" : " AND " + buildConditions(resolver));

        // Query database
        return context.query(query, statement -> {
            mapper.setDirectParameterValue(resolver.getPrimaryKey(), key, statement, 1);
        }, resultSet -> {
            // TODO: Handle multiple responses, throw error
            if (resultSet.next()) {
                Object instance = EntityCreator.fromColumns(resolver.getTargetClass(),
                        resultSet, mapper, context);
                return Optional.of(instance);
            } else {
                return Optional.empty();
            }
        });
    }

    @Override
    public Collection<Object> findByAttributes(AttributeResolver resolver, Map<ColumnAttribute, Object> attributes) {
        // Build query
        List<ColumnAttribute> columnAttributes = new ArrayList<>();
        List<String> conditionArguments = new ArrayList<>();

        for (Map.Entry<ColumnAttribute, Object> entry : attributes.entrySet()) {
            ColumnAttribute column = entry.getKey();
            conditionArguments.add("%s = ?".formatted(column.columnName()));

            columnAttributes.add(column);
        }
        String conditions = String.join(" AND ", conditionArguments);

        String query = "SELECT * FROM %s WHERE %s".formatted(resolver.getFormattedTableNames(), conditions);

        // Query database
        return context.query(query, statement -> {
            int index = 1;
            for (ColumnAttribute column : columnAttributes) {
                mapper.setDirectParameterValue(column, attributes.get(column), statement, index);
                index++;
            }
        }, resultSet -> {
            List<Object> result = new ArrayList<>();

            while (resultSet.next()) {
                Object instance = EntityCreator.fromColumns(resolver.getTargetClass(), resultSet, mapper, context);
                result.add(instance);
            }

            return result;
        });
    }

    @Override
    public void delete(AttributeResolver resolver, Object entity) {
        resolver.getColumns().stream()
                .filter(ColumnAttribute::isCollection)
                .map(column -> new CollectionSupportTable(resolver, column, mapper, context))
                .forEach(table -> {
                    table.deleteExistingElements(entity);
                });

        String query = "DELETE FROM %s WHERE %s = ?"
                .formatted(resolver.getTable(), resolver.getPrimaryKey().columnName());

        context.query(query, statement -> {
            mapper.setParameterValue(resolver.getPrimaryKey(), entity, statement, 1);
        });
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
}