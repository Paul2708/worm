package de.paul2708.worm.repository;

import de.paul2708.worm.database.Database;
import de.paul2708.worm.database.DatabaseActionProcessor;
import de.paul2708.worm.repository.actions.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class RepositoryInvocationHandler implements InvocationHandler {

    private final DatabaseActionProcessor processor;

    public RepositoryInvocationHandler(Class<?> entityClass, Database database) {
        this.processor = new DatabaseActionProcessor(database, entityClass);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.isDefault()) {
            try {
                return InvocationHandler.invokeDefault(proxy, method, args);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        DatabaseAction saveAction = new SaveAction(new MethodInformation(method, args));
        DatabaseAction findAllAction = new FindAllAction(new MethodInformation(method, args));
        DatabaseAction findByIdAction = new FindByIdAction(new MethodInformation(method, args));
        DatabaseAction deleteAction = new DeleteAction(new MethodInformation(method, args));
        DatabaseAction findByAttributesAction = new FindByAttributesAction(new MethodInformation(method, args));

        List<DatabaseAction> actions = List.of(saveAction, findAllAction, findByIdAction, deleteAction,
                findByAttributesAction);

        for (DatabaseAction action : actions) {
            if (action.matches(method, args)) {
                return processor.process(action);
            }
        }

        throw new IllegalArgumentException("No handler found for the given method.");
    }
}