package de.paul2708.worm.database.functionality.mysql;

import de.paul2708.worm.database.functionality.CollectionsTest;
import de.paul2708.worm.database.functionality.DatabaseProvider;
import de.paul2708.worm.database.functionality.IdentifierTest;

public class MySQLIdentifierTest extends IdentifierTest {

    @Override
    public DatabaseProvider provider() {
        return new MySQLDatabaseProvider();
    }
}
