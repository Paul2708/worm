package de.paul2708.worm.database.functionality.mysql;

import de.paul2708.worm.database.functionality.DatabaseProvider;
import de.paul2708.worm.database.functionality.IdentifierTest;
import de.paul2708.worm.database.functionality.ReferenceTest;

public class MySQLReferenceTest extends ReferenceTest {

    @Override
    public DatabaseProvider provider() {
        return new MySQLDatabaseProvider();
    }
}
