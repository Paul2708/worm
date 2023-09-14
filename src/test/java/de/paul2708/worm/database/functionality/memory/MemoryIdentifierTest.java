package de.paul2708.worm.database.functionality.memory;

import de.paul2708.worm.database.functionality.CustomMethodsTest;
import de.paul2708.worm.database.functionality.DatabaseProvider;
import de.paul2708.worm.database.functionality.IdentifierTest;

public class MemoryIdentifierTest extends IdentifierTest {

    @Override
    public DatabaseProvider provider() {
        return new InMemoryDatabaseProvider();
    }
}
