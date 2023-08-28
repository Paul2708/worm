package de.paul2708.worm.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectionsTest {

    private List<Integer> ints;
    private Set<String> strings;

    @Test
    void testListElementTypes() {
        Field intsField = Reflections.getField(ReflectionsTest.class, "ints");
        Field stringsField = Reflections.getField(ReflectionsTest.class, "strings");

        assertEquals(Integer.class, Reflections.getElementType(intsField));
        assertEquals(String.class, Reflections.getElementType(stringsField));
    }

    @Test
    void testListClass() {
        assertTrue(Reflections.isList(List.class));
        assertTrue(Reflections.isList(ArrayList.class));
        assertTrue(Reflections.isList(LinkedList.class));

        assertFalse(Reflections.isList(Set.class));
        assertFalse(Reflections.isList(HashSet.class));
    }

    @Test
    void testSetClass() {
        assertTrue(Reflections.isSet(Set.class));
        assertTrue(Reflections.isSet(HashSet.class));
        assertTrue(Reflections.isSet(TreeSet.class));

        assertFalse(Reflections.isSet(List.class));
        assertFalse(Reflections.isSet(ArrayList.class));
    }
}
