package de.paul2708.worm.database;

import de.paul2708.worm.database.memory.InMemoryDatabase;

public class InMemoryDatabaseTest extends DatabaseTest {

    @Override
    public Database createEmptyDatabase() {
        return new InMemoryDatabase();
    }
}