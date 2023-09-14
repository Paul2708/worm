package de.paul2708.worm.attributes.generator;

import java.util.UUID;

public class UUIDGenerator implements ValueGenerator<UUID> {

    @Override
    public UUID generate() {
        return UUID.randomUUID();
    }
}
