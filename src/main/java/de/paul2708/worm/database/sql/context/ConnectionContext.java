package de.paul2708.worm.database.sql.context;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class ConnectionContext {

    private final DataSource dataSource;

    public ConnectionContext(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void query(String query) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void query(String query, Consumer<PreparedStatement> parameterConsumer) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            parameterConsumer.accept(statement);

            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T query(String query, Consumer<PreparedStatement> parameterConsumer, SQLFunction<T> resultFunction) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            parameterConsumer.accept(statement);

            ResultSet resultSet = statement.executeQuery();
            return resultFunction.apply(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void query(String query, Consumer<PreparedStatement> parameterConsumer, SQLConsumer resultConsumer) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            parameterConsumer.accept(statement);

            ResultSet resultSet = statement.executeQuery();
            resultConsumer.accept(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T query(String query, SQLFunction<T> resultFunction) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            return resultFunction.apply(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
