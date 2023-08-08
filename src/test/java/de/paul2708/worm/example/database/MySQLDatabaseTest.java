package de.paul2708.worm.example.database;

import de.paul2708.worm.database.Database;
import de.paul2708.worm.database.sql.MySQLDatabase;
import org.junit.jupiter.api.Assertions;

import java.sql.*;

public class MySQLDatabaseTest extends DatabaseTest {

    private static final String HOST = "host.docker.internal";
    private static final int PORT = 3306;
    private static final String DATABASE = "worm";
    private static final String USERNAME = "worm";
    private static final String PASSWORD = "worm_password";

    @Override
    public Database createEmptyDatabase() {
        // Drop table
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://%s:%s/%s".formatted(HOST, PORT, DATABASE), USERNAME, PASSWORD);
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS persons");
        } catch (SQLException e) {
            Assertions.fail("Failed to clear tables", e);
        }

        // Create database
        return new MySQLDatabase(HOST, PORT, DATABASE, USERNAME, PASSWORD);
    }
}