package de.paul2708.worm.database.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.paul2708.worm.attributes.AttributeResolver;
import de.paul2708.worm.attributes.AttributeInformation;
import de.paul2708.worm.attributes.CreatedAt;
import de.paul2708.worm.attributes.UpdatedAt;
import de.paul2708.worm.attributes.properties.ReferenceProperty;
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
        String sqlColumns = resolver.getAttributes().stream()
                .filter(column -> !column.isCollection())
                .map(column -> "%s %s".formatted(column.attributeName(), mapper.toSqlType(column)))
                .collect(Collectors.joining(", "));

        String foreignKeyReferences = resolver.getReferences().stream()
                .map(column -> {
                    String table = column.getProperty(ReferenceProperty.class).getReferenceEntity();
                    String primaryKey = column.getProperty(ReferenceProperty.class).getForeignIdentifier().attributeName();

                    return "FOREIGN KEY (%s) REFERENCES %s(%s)"
                            .formatted(column.attributeName(), table, primaryKey);
                })
                .collect(Collectors.joining(", "));

        String query = "CREATE TABLE IF NOT EXISTS %s (%s, PRIMARY KEY (%s)"
                .formatted(resolver.getEntity(), sqlColumns, resolver.getIdentifier().attributeName());

        if (!resolver.getReferences().isEmpty()) {
            query += ", %s".formatted(foreignKeyReferences) + ")";
        } else {
            query += ")";
        }

        context.query(query);

        // Create collection tables
        for (AttributeInformation column : resolver.getAttributes()) {
            if (column.isCollection()) {
                new CollectionSupportTable(resolver, column, mapper, context).create();
            }
        }
    }

    @Override
    public Object save(AttributeResolver resolver, Object entity) {
        // Save entity attributes
        String sqlColumns = resolver.getAttributes().stream()
                .filter(column -> !column.hasAnnotation(CreatedAt.class) && !column.hasAnnotation(UpdatedAt.class) && !column.isCollection())
                .map(AttributeInformation::attributeName)
                .collect(Collectors.joining(", "));
        String sqlValues = resolver.getAttributes().stream()
                .filter(column -> !column.hasAnnotation(CreatedAt.class) && !column.hasAnnotation(UpdatedAt.class)  && !column.isCollection())
                .map(column -> "?")
                .collect(Collectors.joining(", "));
        String sqlUpdate = resolver.getAttributesWithoutIdentifier().stream()
                .filter(column -> !column.hasAnnotation(CreatedAt.class) && !column.hasAnnotation(UpdatedAt.class)  && !column.isCollection())
                .map(AttributeInformation::attributeName)
                .map("%s = ?"::formatted)
                .collect(Collectors.joining(", "));

        for (AttributeInformation column : resolver.getAttributes()) {
            if (column.hasAnnotation(UpdatedAt.class)) {
                sqlUpdate += ", " + column.attributeName() + " = CURRENT_TIMESTAMP(6)";
            }
        }

        String query = "INSERT INTO %s (%s) VALUES (%s)".formatted(resolver.getEntity(), sqlColumns, sqlValues);

        int actualColumns = (int) resolver.getAttributes().stream()
                .filter(columnAttribute -> !columnAttribute.isCollection())
                .count();
        if (actualColumns != 1) {
            query += " ON DUPLICATE KEY UPDATE %s".formatted(sqlUpdate);
        }

        context.query(query, statement -> {
            int index = 1;
            for (AttributeInformation column : resolver.getAttributes()) {
                if (column.isAutoTimestamp() || column.isCollection()) {
                    continue;
                }

                mapper.setParameterValue(column, entity, statement, index);
                index++;
            }
            for (AttributeInformation column : resolver.getAttributesWithoutIdentifier()) {
                if (column.isAutoTimestamp() || column.isCollection()) {
                    continue;
                }

                mapper.setParameterValue(column, entity, statement, index);
                index++;
            }
        });

        // Fetch default column values
        List<AttributeInformation> timestampColumns = resolver.getAttributes().stream()
                .filter(AttributeInformation::isAutoTimestamp)
                .sorted()
                .toList();

        if (!timestampColumns.isEmpty()) {
            String timestampQuery = "SELECT %s FROM %s WHERE %s = ?"
                    .formatted(timestampColumns.stream().map(AttributeInformation::attributeName).collect(Collectors.joining(", ")),
                            resolver.getEntity(),
                            resolver.getIdentifier().attributeName());

            context.query(timestampQuery, statement -> {
                mapper.setParameterValue(resolver.getIdentifier(), entity, statement, 1);
            }, resultSet -> {
                if (resultSet.next()) {
                    for (AttributeInformation column : timestampColumns) {
                        Object timestamp = mapper.getValue(resultSet, column);
                        column.setValue(entity, timestamp);
                    }
                }
            });
        }

        // Save collections
        for (AttributeInformation column : resolver.getAttributes()) {
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
                .formatted(resolver.getFormattedEntityNames(),
                        resolver.getReferences().isEmpty() ? "" : " WHERE " + buildConditions(resolver));

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
                .formatted(resolver.getFormattedEntityNames(),
                        resolver.getIdentifier().getFullAttributeName(),
                        resolver.getReferences().isEmpty() ? "" : " AND " + buildConditions(resolver));

        // Query database
        return context.query(query, statement -> {
            mapper.setDirectParameterValue(resolver.getIdentifier(), key, statement, 1);
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
    public Collection<Object> findByAttributes(AttributeResolver resolver, Map<AttributeInformation, Object> attributes) {
        // Build query
        List<AttributeInformation> attributeInformations = new ArrayList<>();
        List<String> conditionArguments = new ArrayList<>();

        for (Map.Entry<AttributeInformation, Object> entry : attributes.entrySet()) {
            AttributeInformation column = entry.getKey();
            conditionArguments.add("%s = ?".formatted(column.attributeName()));

            attributeInformations.add(column);
        }
        String conditions = String.join(" AND ", conditionArguments);

        String query = "SELECT * FROM %s WHERE %s".formatted(resolver.getFormattedEntityNames(), conditions);

        // Query database
        return context.query(query, statement -> {
            int index = 1;
            for (AttributeInformation column : attributeInformations) {
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
        resolver.getAttributes().stream()
                .filter(AttributeInformation::isCollection)
                .map(column -> new CollectionSupportTable(resolver, column, mapper, context))
                .forEach(table -> {
                    table.deleteExistingElements(entity);
                });

        String query = "DELETE FROM %s WHERE %s = ?"
                .formatted(resolver.getEntity(), resolver.getIdentifier().attributeName());

        context.query(query, statement -> {
            mapper.setParameterValue(resolver.getIdentifier(), entity, statement, 1);
        });
    }

    private String buildConditions(AttributeResolver resolver) {
        return resolver.getReferences().stream()
                .map(column -> {
                    String fullColumnName = column.getProperty(ReferenceProperty.class).getForeignIdentifier()
                            .getFullAttributeName();
                    return column.getFullAttributeName() + " = " + fullColumnName;
                })
                .collect(Collectors.joining(" AND "));
    }
}