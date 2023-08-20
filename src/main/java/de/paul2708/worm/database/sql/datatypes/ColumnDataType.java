package de.paul2708.worm.database.sql.datatypes;

import de.paul2708.worm.columns.ColumnAttribute;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ColumnDataType<T> {

    boolean matches(Class<?> clazz);

    T from(ResultSet resultSet, ColumnAttribute attribute, String column) throws SQLException;

    void to(PreparedStatement statement, int index, ColumnAttribute attribute, T value) throws SQLException;

    @Deprecated
    default void unsafeTo(PreparedStatement statement, int index, ColumnAttribute attribute, Object value) throws SQLException {
        //noinspection unchecked
        to(statement, index, attribute, (T) value);
    }

    /**
     * Convert the column attribute to a sql type.
     *
     * @param attribute column attribute
     * @return sql type
     */
    String getSqlType(ColumnAttribute attribute);
}
