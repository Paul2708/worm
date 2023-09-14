package de.paul2708.worm.database.functionality.memory;

import de.paul2708.worm.database.Database;
import de.paul2708.worm.database.functionality.DatabaseProvider;
import de.paul2708.worm.database.memory.InMemoryDatabase;

public class InMemoryDatabaseProvider implements DatabaseProvider {

    @Override
    public Database createEmptyDatabase() {
        return new InMemoryDatabase();
    }
}
