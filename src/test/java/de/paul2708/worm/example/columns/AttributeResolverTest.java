package de.paul2708.worm.example.columns;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.example.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttributeResolverTest {

    private final AttributeResolver resolver;

    public AttributeResolverTest() {
        this.resolver = new AttributeResolver(Person.class);
    }

    @Test
    void testTableName() {
        assertEquals("persons", resolver.getTable());
    }

    @Test
    void testColumnsOrder() {
        assertEquals(4, resolver.getColumns().size());

        assertEquals("id", resolver.getColumns().get(0).columnName());
        assertEquals("age", resolver.getColumns().get(1).columnName());
        assertEquals("name", resolver.getColumns().get(2).columnName());
        assertEquals("uuid", resolver.getColumns().get(3).columnName());
    }
}