package de.paul2708.worm.repository;

import de.paul2708.worm.database.Database;
import de.paul2708.worm.repository.actions.DatabaseAction;
import de.paul2708.worm.repository.actions.FindAllAction;
import de.paul2708.worm.repository.actions.MethodInformation;
import de.paul2708.worm.repository.actions.SaveAction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class RepositoryInvocationHandler implements InvocationHandler {

    private final Class<?> repositoryClass;
    private final Database database;

    public RepositoryInvocationHandler(Class<?> repositoryClass, Database database) {
        this.repositoryClass = repositoryClass;
        this.database = database;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        DatabaseAction saveAction = new SaveAction(new MethodInformation(method, args));
        DatabaseAction findAllAction = new FindAllAction(new MethodInformation(method, args));

        List<DatabaseAction> actions = List.of(saveAction, findAllAction);

        for (DatabaseAction action : actions) {
            if (action.matches(method, args)) {
                return database.process(action);
            }
        }

        throw new IllegalArgumentException("No handler found for the given method.");
    }
}