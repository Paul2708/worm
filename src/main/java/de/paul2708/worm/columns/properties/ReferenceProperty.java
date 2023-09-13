package de.paul2708.worm.columns.properties;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;

public class ReferenceProperty implements ColumnProperty {

    private final Class<?> reference;

    private final AttributeResolver resolver;

    public ReferenceProperty(Class<?> reference) {
        this.reference = reference;

        this.resolver = new AttributeResolver(reference);
    }

    public Class<?> getReference() {
        return reference;
    }

    public String getForeignTable() {
        return resolver.getTable();
    }

    public ColumnAttribute getForeignIdentifier() {
        return resolver.getIdentifier();
    }
}
