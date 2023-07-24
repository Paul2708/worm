package de.paul2708.worm.database;

import de.paul2708.worm.repository.actions.DatabaseAction;

public interface Database {

    Object process(DatabaseAction action);
}
