package de.paul2708.worm.repository.actions;

import java.lang.reflect.Method;

public abstract class DatabaseAction {

    private final MethodInformation methodInformation;

    public DatabaseAction(MethodInformation methodInformation) {
        this.methodInformation = methodInformation;
    }

    public abstract boolean matches(Method method, Object[] args);

    public MethodInformation getMethodInformation() {
        return methodInformation;
    }
}