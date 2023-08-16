package de.paul2708.worm.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UUIDConverterTest {

    @Test
    void testConversion() {
        UUID uuid = UUID.fromString("cd15f5df-ab7d-4196-8edf-21199cd5ce7f");

        byte[] bytes = UUIDConverter.convert(uuid);
        assertEquals(16, bytes.length);
        
        assertEquals(uuid, UUIDConverter.convert(bytes));
    }
}
