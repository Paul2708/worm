package de.paul2708.worm.repository.actions;

import java.lang.reflect.Method;
import java.util.Set;

public class DeleteAction extends DatabaseAction {

    private static final Set<String> KEYWORDS = Set.of("delete");

    public DeleteAction(MethodInformation methodInformation) {
        super(methodInformation);
    }

    @Override
    public boolean matches(Method method, Object[] args) {
        return KEYWORDS.contains(method.getName()) && method.getParameterCount() == 1;
    }
}
