package de.paul2708.worm.attributes.generator;

import java.util.concurrent.ThreadLocalRandom;

public class IntegerGenerator implements ValueGenerator<Integer> {

    @Override
    public Integer generate() {
        return ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
    }
}
