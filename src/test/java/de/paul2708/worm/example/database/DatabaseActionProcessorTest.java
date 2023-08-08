package de.paul2708.worm.example.database;

import de.paul2708.worm.columns.Column;
import de.paul2708.worm.columns.MaxLength;
import de.paul2708.worm.columns.PrimaryKey;
import de.paul2708.worm.columns.Table;
import de.paul2708.worm.database.DatabaseActionProcessor;
import de.paul2708.worm.database.memory.InMemoryDatabase;
import de.paul2708.worm.repository.actions.MethodInformation;
import de.paul2708.worm.repository.actions.SaveAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DatabaseActionProcessorTest {

    private final DatabaseActionProcessor processor;

    public DatabaseActionProcessorTest() {
        this.processor = new DatabaseActionProcessor(new InMemoryDatabase(), StringEntity.class);
    }

    @Test
    void testValidLengths() {
        Assertions.assertDoesNotThrow(() -> {
            simulateSave(new StringEntity("ABCD", "this is very long"));
        });
    }

    @Test
    void testInvalidLength() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            simulateSave(new StringEntity("ABCD1234", "this is very long"));
        });
    }

    private void simulateSave(StringEntity entity) {
        processor.process(new SaveAction(new MethodInformation(null, new Object[]{entity})));
    }

    @Table("strings")
    public static final class StringEntity {

        @Column("limited_text")
        @PrimaryKey
        @MaxLength(4)
        private String limitedText;

        @Column("arbitrary_text")
        private String arbitraryText;

        public StringEntity() {

        }

        public StringEntity(String limitedText, String arbitraryText) {
            this.limitedText = limitedText;
            this.arbitraryText = arbitraryText;
        }
    }
}