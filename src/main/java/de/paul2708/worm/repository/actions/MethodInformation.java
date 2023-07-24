package de.paul2708.worm.repository.actions;

import java.lang.reflect.Method;

public record MethodInformation(Method method, Object[] args) {

}
