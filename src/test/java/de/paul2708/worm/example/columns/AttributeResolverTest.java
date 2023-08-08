package de.paul2708.worm.example.columns;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.example.Person;
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

    @Test
    void testPrimaryKey() {
        assertTrue(resolver.getPrimaryKey().isPrimaryKey());
        assertEquals("id", resolver.getPrimaryKey().columnName());
    }

    @Test
    void testColumnsWithoutPrimaryKey() {
        List<ColumnAttribute> columns = resolver.getColumnsWithoutPrimaryKey();

        assertEquals(3, columns.size());

        assertEquals("age", columns.get(0).columnName());
        assertEquals("name", columns.get(1).columnName());
        assertEquals("uuid", columns.get(2).columnName());
    }
}