package de.paul2708.worm.database.functionality.mysql;

import de.paul2708.worm.database.functionality.AttributeTypeTest;
import de.paul2708.worm.database.functionality.BasicTest;
import de.paul2708.worm.database.functionality.DatabaseProvider;

public class MySQLBasicTest extends BasicTest {

    @Override
    public DatabaseProvider provider() {
        return new MySQLDatabaseProvider();
    }
}
