package de.paul2708.worm.columns;

import de.paul2708.worm.columns.validator.EntityValidator;
import de.paul2708.worm.columns.validator.InvalidEntityException;
import de.paul2708.worm.Car;
import de.paul2708.worm.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EntityValidatorTest {

    @Test
    void testMissingIdentifier() {
        assertThrows(InvalidEntityException.class, () -> {
            EntityValidator.validate(MissingIdentifierEntity.class);
        });
    }

    @Test
    void testFinalField() {
        assertThrows(InvalidEntityException.class, () -> {
            EntityValidator.validate(FinalColumnEntity.class);
        });
    }

    @Test
    void testMissingEntity() {
        assertThrows(InvalidEntityException.class, () -> {
            EntityValidator.validate(MissingEntityEntity.class);
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

    @Entity("entity")
    private static class MissingIdentifierEntity {

        @Column("id")
        private int id;

        public MissingIdentifierEntity() {

        }
    }

    @Entity("entity")
    private static class FinalColumnEntity {

        @Column("id")
        @Identifier
        private final int id;

        public FinalColumnEntity() {
            this.id = 0;
        }
    }

    private static class MissingEntityEntity {

        @Column("id")
        @Identifier
        private int id;

        public MissingEntityEntity() {

        }
    }

    @Entity("entity")
    private static class MissingEmptyConstructorEntity {

        @Column("id")
        @Identifier
        private int id;

        public MissingEmptyConstructorEntity(int id) {
            this.id = id;
        }
    }
}
