package com.github.dotdot;

import com.github.dotdot.converters.IntConverter;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import static com.github.dotdot.DotDot.*;

public class DotDotTest {

    @Test
    public void simple() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("one",1);

        Integer value = get("one", map);
        assertEquals(new Integer(1), value);
    }

    @Test()
    public void nestedKeysButValueIsNotMap() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("one",1);
        map.put("two",2);
        map.put("three",3);

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
        mapLevel3.put("three",3);

        Map<String, Object> mapLevel2 = new HashMap<String, Object>();
        mapLevel2.put("lastname", "Asgari");
        mapLevel2.put("two",mapLevel3);

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

        value = getInt("one.INVALID.three", map);
        assertNull(value);

        value = getInt("INVALID.two.three", map);
        assertNull(value);
    }
    
    @Test
    public void intKeysTest() {
        Map<Integer, Object> mapLevel3 = new HashMap<Integer, Object>();
        mapLevel3.put(31, 22);
        mapLevel3.put(32,3);

        Map<Integer, Object> mapLevel2 = new HashMap<Integer, Object>();
        mapLevel2.put(21, "Asgari");
        mapLevel2.put(22,mapLevel3);

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
        mapLevel3.put("three",3);

        Map<String, Object> mapLevel2 = new HashMap<String, Object>();
        mapLevel2.put("lastname", "Asgari");
        mapLevel2.put("two",mapLevel3);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("firstname", "Mostafa");
        map.put("one", mapLevel2);

        assertTrue( ensure("one", map) );
        assertTrue( ensure("one.two", map) );
        assertTrue( ensure("one.two.three", map) );
        assertFalse( ensure("unknown_key", map) );
        assertFalse( ensure("one.two.unknown_key", map) );
    }


}
