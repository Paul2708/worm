package de.paul2708.worm.repository.actions;

import java.lang.reflect.Method;

public class FindByAttributesAction extends DatabaseAction {

    public FindByAttributesAction(MethodInformation methodInformation) {
        super(methodInformation);
    }

    @Override
    public boolean matches(Method method, Object[] args) {
        if (!method.getName().startsWith("findBy")) {
            return false;
        }

        String attributes = method.getName().replace("findBy", "");
        if (!attributes.contains("And")) {
            return method.getParameterCount() == 1;
        } else {
            return method.getParameterCount() == attributes.split("And").length;
        }
    }
}
