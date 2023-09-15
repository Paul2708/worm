package de.paul2708.worm.database.sql.context;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLFunction<T> {

    T apply(ResultSet resultSet) throws SQLException;
}
