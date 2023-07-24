package de.paul2708.worm.database;

import java.util.concurrent.ThreadLocalRandom;

public class KeyGenerator {

    public Object generate(Class<?> clazz) {
        if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            return ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        }

        return null;
    }
}
