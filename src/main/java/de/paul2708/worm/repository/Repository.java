package de.paul2708.worm.repository;

import de.paul2708.worm.columns.AttributeResolver;
import de.paul2708.worm.database.Database;

import java.lang.reflect.Proxy;

import static de.paul2708.worm.columns.validator.EntityValidator.validate;

public final class Repository {

    private Repository() {

    }

    public static <T, K, R extends CrudRepository<T, K>> R create(Class<R> repositoryClass, Class<T> entityClass, Database database) {
        validate(entityClass);

        database.prepare(new AttributeResolver(entityClass));

        RepositoryInvocationHandler handler = new RepositoryInvocationHandler(entityClass, database);

        return (R) Proxy.newProxyInstance(
                Repository.class.getClassLoader(), new Class[]{repositoryClass},
                handler);
    }
}
