package de.paul2708.worm.database.functionality.mysql;

import de.paul2708.worm.database.functionality.AttributeTypeTest;
import de.paul2708.worm.database.functionality.DatabaseProvider;

public class MySQLAttributeTypeTest extends AttributeTypeTest {

    @Override
    public DatabaseProvider provider() {
        return new MySQLDatabaseProvider();
    }
}
