package de.paul2708.worm.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectionsTest {

    private List<Integer> ints;
    private Set<String> strings;

    @Test
    void testSingleElementTypes() {
        Field intsField = Reflections.getField(ReflectionsTest.class, "ints");
        Field stringsField = Reflections.getField(ReflectionsTest.class, "strings");

        assertEquals(Integer.class, Reflections.getElementType(intsField));
        assertEquals(String.class, Reflections.getElementType(stringsField));
    }

    private Map<String, Integer> map;

    @Test
    void testMultipleElementTypes() {
        Field mapField = Reflections.getField(ReflectionsTest.class, "map");

        List<Class<?>> elementTypes = Reflections.getElementTypes(mapField);

        assertEquals(2, elementTypes.size());
        assertEquals(String.class, elementTypes.get(0));
        assertEquals(Integer.class, elementTypes.get(1));
    }

    private long[] array;

    @Test
    void testArrayElementType() {
        Field arrayField = Reflections.getField(ReflectionsTest.class, "array");

        assertEquals(long.class, Reflections.getElementTypeFromArray(arrayField));
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

    @Test
    void testMapClass() {
        assertTrue(Reflections.isMap(Map.class));
        assertTrue(Reflections.isMap(HashMap.class));
        assertTrue(Reflections.isMap(TreeMap.class));
        assertTrue(Reflections.isMap(SortedMap.class));

        assertFalse(Reflections.isMap(List.class));
        assertFalse(Reflections.isMap(HashSet.class));
    }
}
