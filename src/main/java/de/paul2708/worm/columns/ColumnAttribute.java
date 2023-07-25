package de.paul2708.worm.columns;

import java.util.Objects;

public class ColumnAttribute {

    private final String columnName;
    private final Class<?> type;

    public ColumnAttribute(String columnName, Class<?> type) {
        this.columnName = columnName;
        this.type = type;
    }

    public String columnName() {
        return columnName;
    }

    public Class<?> type() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ColumnAttribute) obj;
        return Objects.equals(this.columnName, that.columnName) &&
                Objects.equals(this.type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName, type);
    }

    @Override
    public String toString() {
        return "ColumnAttribute[" +
                "columnName=" + columnName + ", " +
                "type=" + type + ']';
    }
}