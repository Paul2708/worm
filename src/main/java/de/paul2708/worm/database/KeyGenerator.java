package de.paul2708.worm.database;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class KeyGenerator {

    private KeyGenerator() {

    }

    public static Object generate(Class<?> clazz) {
        if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            return ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        } else if (clazz.equals(UUID.class)) {
            return UUID.randomUUID();
        }

        throw new IllegalArgumentException("There is no key generator for type %s".formatted(clazz.getName()));
    }
}