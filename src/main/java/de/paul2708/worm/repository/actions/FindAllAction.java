package de.paul2708.worm.repository.actions;

import java.lang.reflect.Method;
import java.util.Set;

public class FindAllAction extends DatabaseAction {

    private static Set<String> KEYWORDS = Set.of("findAll", "getAll");

    public FindAllAction(MethodInformation methodInformation) {
        super(methodInformation);
    }

    @Override
    public boolean matches(Method method, Object[] args) {
        return KEYWORDS.contains(method.getName()) && method.getParameterCount() == 0;
    }
}