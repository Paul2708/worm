package de.paul2708.worm.database.functionality.mysql;

import de.paul2708.worm.database.functionality.BasicTest;
import de.paul2708.worm.database.functionality.CollectionsTest;
import de.paul2708.worm.database.functionality.DatabaseProvider;

public class MySQLCollectionsTest extends CollectionsTest {

    @Override
    public DatabaseProvider provider() {
        return new MySQLDatabaseProvider();
    }
}
