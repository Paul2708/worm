package de.paul2708.worm.database;

import de.paul2708.worm.attributes.*;
import de.paul2708.worm.attributes.generator.ValueGenerator;
import de.paul2708.worm.database.memory.InMemoryDatabase;
import de.paul2708.worm.repository.actions.MethodInformation;
import de.paul2708.worm.repository.actions.SaveAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseActionProcessorTest {

    private final DatabaseActionProcessor processor;

    public DatabaseActionProcessorTest() {
        this.processor = new DatabaseActionProcessor(new InMemoryDatabase(), StringEntity.class);
    }

    @Test
    void testValidLengths() {
        assertDoesNotThrow(() -> {
            simulateSave(new StringEntity("ABCD", "this is very long"));
        });
    }

    @Test
    void testInvalidLength() {
        assertThrows(IllegalStateException.class, () -> {
            simulateSave(new StringEntity("ABCD1234", "this is very long"));
        });
    }

    @Test
    void testCustomValueGenerator() {
        StringEntity entity = (StringEntity) simulateSave(new StringEntity("abcd", "random text"));

        assertEquals("auto generated", entity.id);
    }

    private Object simulateSave(StringEntity entity) {
        return processor.process(new SaveAction(new MethodInformation(null, new Object[]{entity})));
    }

    @Entity("strings")
    public static final class StringEntity {

        @Identifier(generator = ConstantValueGenerator.class)
        @Attribute("limited_text")
        private String id;

        @MaxLength(4)
        @Attribute("limited_text")
        private String limited_text;

        @Attribute("arbitrary_text")
        private String arbitraryText;

        public StringEntity() {

        }

        public StringEntity(String limited_text, String arbitraryText) {
            this.limited_text = limited_text;
            this.arbitraryText = arbitraryText;
        }
    }

    public static class ConstantValueGenerator implements ValueGenerator<String> {

        @Override
        public String generate() {
            return "auto generated";
        }
    }
}