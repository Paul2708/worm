package de.paul2708.worm.attributes;

import de.paul2708.worm.attributes.validator.EntityValidator;
import de.paul2708.worm.attributes.validator.InvalidEntityException;
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
            EntityValidator.validate(FinalAttributeEntity.class);
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

        @Attribute("id")
        private int id;

        public MissingIdentifierEntity() {

        }
    }

    @Entity("entity")
    private static class FinalAttributeEntity {

        @Attribute("id")
        @Identifier
        private final int id;

        public FinalAttributeEntity() {
            this.id = 0;
        }
    }

    private static class MissingEntityEntity {

        @Attribute("id")
        @Identifier
        private int id;

        public MissingEntityEntity() {

        }
    }

    @Entity("entity")
    private static class MissingEmptyConstructorEntity {

        @Attribute("id")
        @Identifier
        private int id;

        public MissingEmptyConstructorEntity(int id) {
            this.id = id;
        }
    }
}
