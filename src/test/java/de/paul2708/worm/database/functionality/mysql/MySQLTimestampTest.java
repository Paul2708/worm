package de.paul2708.worm.database.functionality.mysql;

import de.paul2708.worm.database.functionality.DatabaseProvider;
import de.paul2708.worm.database.functionality.ReferenceTest;
import de.paul2708.worm.database.functionality.TimestampTest;

public class MySQLTimestampTest extends TimestampTest {

    @Override
    public DatabaseProvider provider() {
        return new MySQLDatabaseProvider();
    }
}
