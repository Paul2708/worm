package de.paul2708.worm.columns;

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
    void testTableName() {
        assertEquals("persons", resolver.getTable());
    }

    @Test
    void testColumnsOrder() {
        assertEquals(4, resolver.getColumns().size());

        assertEquals("id", resolver.getColumns().get(0).columnName());
        assertEquals("age", resolver.getColumns().get(1).columnName());
        assertEquals("blocked", resolver.getColumns().get(2).columnName());
        assertEquals("name", resolver.getColumns().get(3).columnName());
    }

    @Test
    void testIdentifier() {
        assertTrue(resolver.getIdentifier().isIdentifier());
        assertEquals("id", resolver.getIdentifier().columnName());
    }

    @Test
    void testColumnsWithoutIdentifier() {
        List<ColumnAttribute> columns = resolver.getColumnsWithoutIdentifier();

        assertEquals(3, columns.size());

        assertEquals("age", columns.get(0).columnName());
        assertEquals("blocked", columns.get(1).columnName());
        assertEquals("name", columns.get(2).columnName());
    }

    @Test
    void testFormattedTableName() {
        assertEquals("persons", resolver.getFormattedTableNames());
    }

    @Test
    void testFormattedTableNames() {
        assertEquals("fleets, persons", new AttributeResolver(Fleet.class).getFormattedTableNames());
    }
}