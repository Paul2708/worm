package de.paul2708.worm.attributes.properties;

import de.paul2708.worm.attributes.AttributeResolver;
import de.paul2708.worm.attributes.AttributeInformation;

public class ReferenceProperty implements AttributeProperty {

    private final Class<?> reference;

    private final AttributeResolver resolver;

    public ReferenceProperty(Class<?> reference) {
        this.reference = reference;

        this.resolver = new AttributeResolver(reference);
    }

    public Class<?> getReference() {
        return reference;
    }

    public String getReferenceEntity() {
        return resolver.getEntity();
    }

    public AttributeInformation getForeignIdentifier() {
        return resolver.getIdentifier();
    }
}
