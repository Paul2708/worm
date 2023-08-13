package de.paul2708.worm.columns.properties;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;

public class ForeignKeyProperty implements ColumnProperty {

    private final Class<?> foreignKey;

    private final AttributeResolver resolver;

    public ForeignKeyProperty(Class<?> foreignKey) {
        this.foreignKey = foreignKey;

        this.resolver = new AttributeResolver(foreignKey);
    }

    public Class<?> getForeignKey() {
        return foreignKey;
    }

    public String getForeignTable() {
        return resolver.getTable();
    }

    public ColumnAttribute getForeignPrimaryKey() {
        return resolver.getPrimaryKey();
    }

    public Object getForeignPrimaryKeyValue(Object entity) {
        return resolver.getPrimaryKey().getValue(new AttributeResolver(entity.getClass()).getPrimaryKey().getValue(entity));
    }
}
