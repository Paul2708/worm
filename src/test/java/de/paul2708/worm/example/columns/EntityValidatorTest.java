package de.paul2708.worm.example.columns;

import de.paul2708.worm.columns.Column;
import de.paul2708.worm.columns.PrimaryKey;
import de.paul2708.worm.columns.Table;
import de.paul2708.worm.columns.validator.EntityValidator;
import de.paul2708.worm.columns.validator.InvalidEntityException;
import de.paul2708.worm.example.Car;
import de.paul2708.worm.example.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EntityValidatorTest {

    @Test
    void testMissingPrimaryKey() {
        assertThrows(InvalidEntityException.class, () -> {
            EntityValidator.validate(MissingPrimaryKeyEntity.class);
        });
    }

    @Test
    void testFinalField() {
        assertThrows(InvalidEntityException.class, () -> {
            EntityValidator.validate(FinalColumnEntity.class);
        });
    }

    @Test
    void testMissingTable() {
        assertThrows(InvalidEntityException.class, () -> {
            EntityValidator.validate(MissingTableEntity.class);
        });
    }

    @Test
    void testMissingEmptyConstructor() {
        assertThrows(InvalidEntityException.class, () -> {
            EntityValidator.validate(MissingEmptyConstructorEntity.class);
        });
    }

    @Test
    void testValidEntities() {
        EntityValidator.validate(Person.class);
        EntityValidator.validate(Car.class);
    }

    @Table("entity")
    private static class MissingPrimaryKeyEntity {

        @Column("id")
        private int id;

        public MissingPrimaryKeyEntity() {

        }
    }

    @Table("entity")
    private static class FinalColumnEntity {

        @Column("id")
        @PrimaryKey
        private final int id;

        public FinalColumnEntity() {
            this.id = 0;
        }
    }

    private static class MissingTableEntity {

        @Column("id")
        @PrimaryKey
        private int id;

        public MissingTableEntity() {

        }
    }

    @Table("entity")
    private static class MissingEmptyConstructorEntity {

        @Column("id")
        @PrimaryKey
        private int id;

        public MissingEmptyConstructorEntity(int id) {
            this.id = id;
        }
    }
}
