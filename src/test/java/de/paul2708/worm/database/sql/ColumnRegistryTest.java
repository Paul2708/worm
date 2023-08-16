package de.paul2708.worm.database.sql;

import de.paul2708.worm.columns.ColumnAttribute;
import de.paul2708.worm.database.sql.datatypes.ColumnDataType;
import de.paul2708.worm.database.sql.datatypes.ColumnsRegistry;
import de.paul2708.worm.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ColumnRegistryTest {

    private ColumnsRegistry registry;

    @BeforeEach
    void setUp() {
        this.registry = ColumnsRegistry.create();
        this.registry.init();
    }

    @Test
    void testRegisteredDataType() {
        assertDoesNotThrow(() -> {
            registry.getDataType(String.class);
        });
    }

    @Test
    void testUnregisteredDataType() {
        assertThrows(IllegalArgumentException.class, () -> {
            registry.getDataType(Unregistered.class);
        });
    }

    @Test
    void testRegister() {
        registry.register(new UnregisteredDataType());

        assertDoesNotThrow(() -> {
            registry.getDataType(Unregistered.class);
        });
    }

    @Test
    void testForeignKeyDataType() {
        assertDoesNotThrow(() -> {
            registry.getDataType(Person.class);
        });
    }

    private static class Unregistered {

    }

    private static class UnregisteredDataType implements ColumnDataType<Unregistered> {

        @Override
        public boolean matches(Class<?> clazz) {
            return clazz.equals(Unregistered.class);
        }

        @Override
        public Unregistered from(ResultSet resultSet, String column) {
            return null;
        }

        @Override
        public void to(PreparedStatement statement, int index, Unregistered value) {

        }

        @Override
        public String getSqlType(ColumnAttribute attribute) {
            return null;
        }
    }
}
