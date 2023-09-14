package de.paul2708.worm.attributes;

import de.paul2708.worm.Fleet;
import de.paul2708.worm.Person;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttributeResolverTest {

    private final AttributeResolver resolver;

    public AttributeResolverTest() {
        this.resolver = new AttributeResolver(Person.class);
    }

    @Test
    void testEntityName() {
        assertEquals("persons", resolver.getEntity());
    }

    @Test
    void testAttributeOrder() {
        assertEquals(4, resolver.getAttributes().size());

        assertEquals("id", resolver.getAttributes().get(0).attributeName());
        assertEquals("age", resolver.getAttributes().get(1).attributeName());
        assertEquals("blocked", resolver.getAttributes().get(2).attributeName());
        assertEquals("name", resolver.getAttributes().get(3).attributeName());
    }

    @Test
    void testIdentifier() {
        assertTrue(resolver.getIdentifier().isIdentifier());
        assertEquals("id", resolver.getIdentifier().attributeName());
    }

    @Test
    void testAttributesWithoutIdentifier() {
        List<AttributeInformation> attributes = resolver.getAttributesWithoutIdentifier();

        assertEquals(3, attributes.size());

        assertEquals("age", attributes.get(0).attributeName());
        assertEquals("blocked", attributes.get(1).attributeName());
        assertEquals("name", attributes.get(2).attributeName());
    }

    @Test
    void testFormattedEntityName() {
        assertEquals("persons", resolver.getFormattedEntityNames());
    }

    @Test
    void testFormattedEntityNames() {
        assertEquals("fleets, persons", new AttributeResolver(Fleet.class).getFormattedEntityNames());
    }
}