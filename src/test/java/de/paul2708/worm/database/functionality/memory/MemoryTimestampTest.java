package de.paul2708.worm.database.functionality.memory;

import de.paul2708.worm.database.functionality.DatabaseProvider;
import de.paul2708.worm.database.functionality.ReferenceTest;
import de.paul2708.worm.database.functionality.TimestampTest;

public class MemoryTimestampTest extends TimestampTest {

    @Override
    public DatabaseProvider provider() {
        return new InMemoryDatabaseProvider();
    }
}
