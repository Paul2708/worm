package de.paul2708.worm.columns.properties;

public class ForeignKeyProperty implements ColumnProperty {

    private final Class<?> foreignKey;

    public ForeignKeyProperty(Class<?> foreignKey) {
        this.foreignKey = foreignKey;
    }

    public Class<?> getForeignKey() {
        return foreignKey;
    }
}
