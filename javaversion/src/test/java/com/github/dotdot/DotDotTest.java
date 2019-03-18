package com.github.dotdot;

import com.github.dotdot.converters.IntConverter;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import static com.github.dotdot.DotDot.*;

public class DotDotTest {

    @Test
    public void simple() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("one", 1);

        Integer value = get("one", map);
        assertEquals(new Integer(1), value);
    }

    @Test()
    public void nestedKeysButValueIsNotMap() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);

        try {
            get("one.two.three", map);
            fail("Expected exception");
        } catch (Exception exc) {
            assertEquals(IllegalStateException.class, exc.getClass());
            assertEquals("Cannot move deeper for these keys: two.three", exc.getMessage());
        }
    }

    @Test()
    public void nestedKeys() {
        Map<String, Object> mapLevel3 = new HashMap<String, Object>();
        mapLevel3.put("age", 22);
        mapLevel3.put("three", 3);

        Map<String, Object> mapLevel2 = new HashMap<String, Object>();
        mapLevel2.put("lastname", "Asgari");
        mapLevel2.put("two", mapLevel3);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("firstname", "Mostafa");
        map.put("one", mapLevel2);

        String firstname = getString("firstname", map);
        assertEquals("Mostafa", firstname);

        String lastname = getString("one.lastname", map);
        assertEquals("Asgari", lastname);

        int age = getInt("one.two.age", map);
        assertEquals(22, age);

        Integer value = getInt("one.two.three", map);
        assertEquals(new Integer(3), value);

        value = getInt("one.two.INVALID", map);
        assertNull(value);
        assertEquals(new Integer(0), getInt("one.two.INVALID", map, 0));

        value = getInt("one.INVALID.three", map);
        assertNull(value);
        assertEquals(new Integer(1), getInt("one.INVALID.three", map, 1));

        value = getInt("INVALID.two.three", map);
        assertNull(value);
        assertEquals(new Integer(2), getInt("INVALID.two.three", map, 2));
    }

    @Test
    public void intKeysTest() {
        Map<Integer, Object> mapLevel3 = new HashMap<Integer, Object>();
        mapLevel3.put(31, 22);
        mapLevel3.put(32, 3);

        Map<Integer, Object> mapLevel2 = new HashMap<Integer, Object>();
        mapLevel2.put(21, "Asgari");
        mapLevel2.put(22, mapLevel3);

        Map<Integer, Object> map = new HashMap<Integer, Object>();
        map.put(1, "Mostafa");
        map.put(2, mapLevel2);

        IntConverter converter = new IntConverter();
        assertEquals("Mostafa", getString("1", map, converter));
        assertEquals(new Integer(22), getInt("2.22.31", map, converter));

        Map<Integer, Object> value = getMap("2.22", map, converter);
        assertEquals(mapLevel3, value);
    }

    @Test()
    public void ensureTest() {
        Map<String, Object> mapLevel3 = new HashMap<String, Object>();
        mapLevel3.put("age", 22);
        mapLevel3.put("three", 3);

        Map<String, Object> mapLevel2 = new HashMap<String, Object>();
        mapLevel2.put("lastname", "Asgari");
        mapLevel2.put("two", mapLevel3);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("firstname", "Mostafa");
        map.put("one", mapLevel2);

        assertTrue(ensure("one", map));
        assertTrue(ensure("one.two", map));
        assertTrue(ensure("one.two.three", map));
        assertFalse(ensure("unknown_key", map));
        assertFalse(ensure("one.two.unknown_key", map));
    }

    @Test()
    public void putTest() {
        Map<String, Object> map = new HashMap<String, Object>();

        put("a.b.c.d1", 12, map);
        put("a.b.c.d2", "Hello", map);
        put("a.b2", 3.141592, map);
        put("a.b.c.d3.A", "Mostafa", map);
        put("b", 100, map);

        assertTrue(ensure("a.b.c.d1", map));
        assertTrue(ensure("a.b.c.d2", map));
        assertTrue(ensure("a.b2", map));
        assertTrue(ensure("a.b.c.d3.A", map));
        assertTrue(ensure("b", map));

        assertEquals(12, get("a.b.c.d1", map));
        assertEquals("Hello", get("a.b.c.d2", map));
        assertEquals(3.141592, get("a.b2", map));
        assertEquals("Mostafa", get("a.b.c.d3.A", map));
        assertEquals(100, get("b", map));
    }

    @Test
    public void copyIncludeTest() {
        Map<String, Object> map = new HashMap<String, Object>();

        put("a.b.c.d1", 12, map);
        put("a.b.c.d2", "Hello", map);
        put("a.b2", 3.141592, map);
        put("a.b.c.d3.A", "Mostafa", map);
        put("b", 100, map);

        Map<String, Object> copiedMap = copyInclude(map, Arrays.asList("b", "a.b.c.d3.A"));
        assertEquals(2, copiedMap.size());
        assertEquals("Mostafa", get("a.b.c.d3.A", copiedMap));
        assertEquals(100, get("b", copiedMap));
    }

    @Test
    public void mustNotThrowExceptionTest() throws NoValueException {
        Map<String, Object> map = new HashMap<String, Object>();

        put("a.b.c.d1", 12, map);
        put("a.b.c.d2", "Hello", map);
        put("a.b2", 3.141592, map);
        put("a.b.c.d3.A", "Mostafa", map);
        put("b", 100, map);

        must("a.b.c.d1", map);
        must("a.b.c.d2", map);
        must("a.b2", map);
        must("a.b.c.d3.A", map);
        must("b", map);
    }

    @Test
    public void mustThrowExceptionTest() {
        Map<String, Object> map = new HashMap<String, Object>();

        put("a.b.c.d1", 12, map);
        put("a.b.c.d2", "Hello", map);
        put("a.b2", 3.141592, map);
        put("a.b.c.d3.A", "Mostafa", map);
        put("b", 100, map);

        try {
            must("a.b.c.d4", map);
            fail("a.b.c.d4 has not provided");
        } catch (NoValueException e) {

        }

        try {
            must("a.b", map);
        } catch (NoValueException e) {
            fail("a.b provided");
        }

        try {
            must("c", map);
            fail("there is no c");
        } catch (NoValueException e) {
        }
    }

    @Test
    public void mustEqualTest() throws NotEqualException {
        Map<String, Object> map = new HashMap<String, Object>();

        put("a.b.c.d1", 12, map);
        put("a.b.c.d2", "Hello", map);
        put("a.b2", 3.141592, map);
        put("a.b.c.d3.A", "Mostafa", map);
        put("b", 100, map);

        mustEqual("a.b.c.d1", 12, map);
        mustEqual("a.b.c.d2", "Hello", map);
        mustEqual("a.b2", 3.141592, map);
        mustEqual("a.b.c.d3.A", "Mostafa", map);

        mustEqual("a.b.c.d3.B", null, map);

        try {
            mustEqual("a.b.c.d1", 85, map);
            fail("must throw exception");
        } catch (NotEqualException exc) {

        }

        try {
            mustEqual("a.b.c.B", 85, map);
            fail("must throw exception");
        } catch (NotEqualException exc) {

        }
    }

    @Test
    public void putAsArrayOfKeyValueTest() throws NotEqualException {
        Map<String, Object> map = new HashMap<String, Object>();
        put("a", 12, map);

        Map<String, Object> subMap = new HashMap<String, Object>();
        subMap.put("name", "Mostafa");
        subMap.put("age", 20);

        putAsArrayOfKeyValue("info", subMap, map);

        Map<String, Object>[] arr = (Map[])get("info", map);
        assertEquals(2, arr.length);

        assertEquals("name", arr[0].get("key"));
        assertEquals("Mostafa", arr[0].get("value"));

        assertEquals("age", arr[1].get("key"));
        assertEquals(20, arr[1].get("value"));
    }

    @Test
    public void putAsArrayOfKeyValueNullTest() {
        Map<String, Object> map = new HashMap<String, Object>();
        put("a", 12, map);

        putAsArrayOfKeyValue("info", null, map);
        assertEquals(2, map.size());
        assertEquals(null, map.get("info"));
    }
}