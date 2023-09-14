package de.paul2708.worm.attributes.properties;

import de.paul2708.worm.attributes.generator.NoneGenerator;
import de.paul2708.worm.attributes.generator.ValueGenerator;

import java.lang.reflect.InvocationTargetException;

public class IdentifierProperty implements AttributeProperty {

    private final Class<? extends ValueGenerator<?>> generatorClazz;

    public IdentifierProperty(Class<? extends ValueGenerator<?>> generatorClazz) {
        this.generatorClazz = generatorClazz;
    }

    public boolean hasGenerator() {
        return !generatorClazz.equals(NoneGenerator.class);
    }

    public ValueGenerator<?> createGenerator() {
        try {
            return generatorClazz.getConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}