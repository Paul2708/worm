package de.paul2708.worm.database.functionality.mysql;

import de.paul2708.worm.database.functionality.CollectionsTest;
import de.paul2708.worm.database.functionality.CustomMethodsTest;
import de.paul2708.worm.database.functionality.DatabaseProvider;

public class MySQLCustomMethodsTest extends CustomMethodsTest {

    @Override
    public DatabaseProvider provider() {
        return new MySQLDatabaseProvider();
    }
}
