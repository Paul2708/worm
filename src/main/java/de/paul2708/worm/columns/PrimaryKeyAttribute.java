package de.paul2708.worm.columns;

public record PrimaryKeyAttribute(String fieldName, String columnName, Class<?> fieldType, boolean autoGenerated) {

}
