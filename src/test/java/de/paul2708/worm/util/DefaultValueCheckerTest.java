package de.paul2708.worm.util;

import de.paul2708.worm.util.DefaultValueChecker;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultValueCheckerTest {

    @Test
    void testUninitializedFields() {
        DefaultValueContainer container = new DefaultValueContainer();

        assertTrue(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aByte")));
        assertTrue(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aShort")));
        assertTrue(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "anInt")));
        assertTrue(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aLong")));
        assertTrue(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aFloat")));
        assertTrue(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aDouble")));
        assertTrue(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aChar")));
        assertTrue(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aString")));
        assertTrue(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aBoolean")));
    }

    @Test
    void testInitializedFields() {
        DefaultValueContainer container = new DefaultValueContainer((byte) 1, (short) 1, 1, 1L, 1.0f, 1.0d, 'A', "A",
                true);

        assertFalse(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aByte")));
        assertFalse(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aShort")));
        assertFalse(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "anInt")));
        assertFalse(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aLong")));
        assertFalse(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aFloat")));
        assertFalse(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aDouble")));
        assertFalse(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aChar")));
        assertFalse(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aString")));
        assertFalse(DefaultValueChecker.isDefaultValue(container, getFieldByName(container, "aBoolean")));
    }

    private Field getFieldByName(Object object, String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);

            return field;
        } catch (NoSuchFieldException e) {
            fail(e);
            return null;
        }
    }

    public static class DefaultValueContainer {

        private byte aByte;
        private short aShort;
        private int anInt;
        private long aLong;
        private float aFloat;
        private double aDouble;
        private char aChar;
        private String aString;
        private boolean aBoolean;

        public DefaultValueContainer() {

        }

        public DefaultValueContainer(byte aByte, short aShort, int anInt, long aLong, float aFloat, double aDouble, char aChar, String aString, boolean aBoolean) {
            this.aByte = aByte;
            this.aShort = aShort;
            this.anInt = anInt;
            this.aLong = aLong;
            this.aFloat = aFloat;
            this.aDouble = aDouble;
            this.aChar = aChar;
            this.aString = aString;
            this.aBoolean = aBoolean;
        }
    }
}