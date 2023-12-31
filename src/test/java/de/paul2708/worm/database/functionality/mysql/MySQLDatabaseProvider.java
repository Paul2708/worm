package de.paul2708.worm.database.functionality.mysql;

import de.paul2708.worm.database.Database;
import de.paul2708.worm.database.functionality.DatabaseProvider;
import de.paul2708.worm.database.sql.MySQLDatabase;
import org.junit.jupiter.api.Assertions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLDatabaseProvider implements DatabaseProvider {

    private static final String HOST = "host.docker.internal";
    private static final int PORT = 3306;
    private static final String DATABASE = "worm";
    private static final String USERNAME = "worm";
    private static final String PASSWORD = "worm_password";

    @Override
    public Database createEmptyDatabase() {
        // Drop tables
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://%s:%s/%s".formatted(HOST, PORT, DATABASE), USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS persons, cars, fleets, rounds, collectors, collectors_badges, " +
                    "collectors_primes, basic_entities, collection_entities, collection_entities_mapping, "
                    + "collection_entities_array, small_entities");
        } catch (SQLException e) {
            Assertions.fail("Failed to clear tables", e);
        }

        // Create database
        return new MySQLDatabase(HOST, PORT, DATABASE, USERNAME, PASSWORD);
    }

    // TODO: Close connection
}
