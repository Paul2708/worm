package de.paul2708.worm.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReflectionsTest {

    private List<Integer> ints;
    private Set<String> strings;

    @Test
    void test() {
        Field intsField = Reflections.getField(ReflectionsTest.class, "ints");
        Field stringsField = Reflections.getField(ReflectionsTest.class, "strings");

        assertEquals(Integer.class, Reflections.getElementType(intsField));
        assertEquals(String.class, Reflections.getElementType(stringsField));
    }
}
