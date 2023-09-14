package de.paul2708.worm.attributes.generator;

public class NoneGenerator implements ValueGenerator<Object> {

    @Override
    public Object generate() {
        throw new IllegalStateException("This generator is only present if no generator is set.");
    }
}
