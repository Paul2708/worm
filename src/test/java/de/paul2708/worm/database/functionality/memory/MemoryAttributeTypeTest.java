package de.paul2708.worm.database.functionality.memory;

import de.paul2708.worm.database.functionality.AttributeTypeTest;
import de.paul2708.worm.database.functionality.DatabaseProvider;

public class MemoryAttributeTypeTest extends AttributeTypeTest {

    @Override
    public DatabaseProvider provider() {
        return new InMemoryDatabaseProvider();
    }
}
