package de.paul2708.worm.database.sql.context;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLConsumer {

    void accept(ResultSet resultSet) throws SQLException;
}
