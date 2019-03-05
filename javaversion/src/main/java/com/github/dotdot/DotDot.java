package com.github.dotdot;

import com.github.dotdot.converters.Converter;
import com.github.dotdot.converters.StringConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DotDot {

    /**
     * Returns the value of last key
     * Keys should separated by dot in the format of keyLevel1.keyLevel2.keyLevelK
     * In this case if all keys have value then it will return the value of last key 'keyLevelK'
     * Otherwise return null.
     * @param path nested keys that separated by dot(.)
     * @param map your key/value map
     * @param converter converter for converting string representation of the path to your key type
     * @param <K> the key type
     * @param <V> the value type
     * @throws IllegalStateException if your path is nested but your map value is not a Map
     */
    public static <K,V> V get(String path, Map<K,V> map, Converter<K> converter) {
        if (map == null) {
            return null;
        }

        String[] keys = path.split("\\.");
        Map<K,V> subMap = map;

        for (int i = 0; i < keys.length; i++) {
            V value = subMap.get(converter.convert(keys[i]));

            // if this is the last key
            // just return the value of it
            if (i == keys.length-1) {
                return value;
            }

            if (value == null) {
                return null;
            }

            if (value instanceof Map){
                subMap = (Map<K,V>)value;
            } else {
                String remainingKey = join(keys, i+1);
                throw new IllegalStateException("Cannot move deeper for these keys: " + remainingKey);
            }
        }

        return null;
    }

    /**
     * Ensures that the given path has some value
     * @param path nested keys that separated by dot(.)
     * @param map your key/value map
     * @param converter converter for converting string representation of the path to your key type
     * @param <K> the key type
     * @param <V> the value type
     * @throws IllegalStateException if your path is nested but your map's value is not a Map
     */
    public static <K,V> boolean ensure(String path, Map<K,V> map, Converter<K> converter) {
        V value = get(path, map, converter);
        if (value == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Must is like `Ensure` method but it will throw exception if
     * the given path has not value
     * @param path nested keys that separated by dot(.)
     * @param map your key/value map
     * @param converter converter for converting string representation of the path to your key type
     * @param <K> the key type
     * @param <V> the value type
     * @throws IllegalStateException if your path is nested but your map's value is not a Map
     * @throws NoValueException If the given path has not value
     */
    public static <K,V> void must(String path, Map<K,V> map, Converter<K> converter) throws NoValueException {
        V value = get(path, map, converter);
        if (value == null) {
            throw new NoValueException(path + " value is missing");
        }
    }

    /**
     * Check the given path has exact value
     * @param path nested keys that separated by dot(.)
     * @param expected the value that must be provided
     * @param map your key/value map
     * @param converter converter for converting string representation of the path to your key type
     * @param <K> the key type
     * @param <V> the value type
     * @throws IllegalStateException if your path is nested but your map's value is not a Map
     * @throws NotEqualException If the values mismatch
     */
    public static <K,V> void mustEqual(String path, V expected, Map<K,V> map, Converter<K> converter) throws NotEqualException {
        V value = get(path, map, converter);

        if (value == null && expected == null) {
            // match
            return;
        }

        if (value == null) {
            throw new NotEqualException("null is not equal to " + expected);
        }

        if (!value.equals(expected)) {
            throw new NotEqualException(value.toString() + " is not equal to " + expected);
        }
    }

    /**
     * Create a new Map that only contains keys inside `includes` list
     */
    public static <K> Map<K, Object> copyInclude(Map<K, Object> map,
                                                 List<String> includes,
                                                 Converter<K> converter) {
        if (map == null)
            return null;

        Map<K, Object> result = null;
        try {
            result = map.getClass().newInstance();
        } catch (Exception e) {
            result = new HashMap<K, Object>(includes.size());
        }

        for(String path: includes) {
            put(path, get(path, map, converter), result, converter);
        }

        return result;
    }

    public static Map<String, Object> copyInclude(Map<String, Object> map, List<String> includes) {
        return copyInclude(map, includes, new StringConverter());
    }

    public static <K> void put(String path, Object value, Map<K,Object> map, Converter<K> converter) {
        String[] keys = path.split("\\.");

        if (keys.length == 1) {
            map.put(converter.convert(keys[0]), value);
            return;
        }

        Map<K, Object>[] maps = new Map[keys.length];
        int size = maps.length;
        String prevKey = "";

        for (int i = 0; i < size; i++) {

            if (i == 0) {
                maps[i] = map;
                continue;
            }

            if (prevKey.equals(""))
                prevKey += keys[i-1];
            else
                prevKey += "." + keys[i-1];

            Object mapValue = get(prevKey, map, converter);
            K key = converter.convert(keys[i - 1]);
            if (mapValue == null) {
                try {
                    maps[i] = map.getClass().newInstance();
                } catch (Exception e) {
                    maps[i] = new HashMap<K, Object>();
                }

                maps[i - 1].put(key, maps[i]);
            } else {
                maps[i] = (Map)mapValue;
            }
        }

        maps[size-1].put(converter.convert(keys[size-1]), value);
    }

    public static void put(String path, Object value, Map<String,Object> map) {
        put(path, value, map, new StringConverter());
    }

    public static <V> boolean ensure(String path, Map<String,V> map) {
        return ensure(path, map, new StringConverter());
    }

    public static <V> void must(String path, Map<String,V> map) throws NoValueException {
        must(path, map, new StringConverter());
    }

    public static <V> void mustEqual(String path, V expected, Map<String,V> map) throws NotEqualException {
        mustEqual(path, expected, map, new StringConverter());
    }

    public static <V> String getString(String path, Map<String,V> map) {
        return (String) get(path, map, new StringConverter());
    }
    public static <V> String getString(String path, Map<String,V> map, String defaultValue) {
        String original = (String)get(path, map, new StringConverter());
        return original != null ? original : defaultValue;
    }
    public static <K,V> String getString(String path, Map<K,V> map, Converter<K> converter) {
        return (String) get(path, map, converter);
    }

    public static <V> Character getChar(String path, Map<String,V> map) {
        return (Character) get(path, map, new StringConverter());
    }
    public static <V> Character getChar(String path, Map<String,V> map, Character defaultValue) {
        Character original = (Character) get(path, map, new StringConverter());
        return original != null ? original : defaultValue;
    }
    public static <K,V> Character getChar(String path, Map<K,V> map, Converter<K> converter) {
        return (Character) get(path, map, converter);
    }

    public static <V> Byte getByte(String path, Map<String,V> map) {
        return (Byte) get(path, map, new StringConverter());
    }
    public static <V> Byte getByte(String path, Map<String,V> map, Byte defaultValue) {
        Byte original = (Byte) get(path, map, new StringConverter());
        return original != null ? original : defaultValue;
    }
    public static <K,V> Byte getByte(String path, Map<K,V> map, Converter<K> converter) {
        return (Byte) get(path, map, converter);
    }

    public static <V> Short getShort(String path, Map<String,V> map) {
        return (Short) get(path, map, new StringConverter());
    }
    public static <V> Short getShort(String path, Map<String,V> map, Short defaultValue) {
        Short original = (Short) get(path, map, new StringConverter());
        return original != null ? original : defaultValue;
    }
    public static <K,V> Short getShort(String path, Map<K,V> map, Converter<K> converter) {
        return (Short) get(path, map, converter);
    }

    public static <V> Integer getInt(String path, Map<String,V> map) {
        return (Integer) get(path, map, new StringConverter());
    }
    public static <V> Integer getInt(String path, Map<String,V> map, Integer defaultValue) {
        Integer original = (Integer) get(path, map, new StringConverter());
        return original != null ? original : defaultValue;
    }
    public static <K,V> Integer getInt(String path, Map<K,V> map, Converter<K> converter) {
        return (Integer) get(path, map, converter);
    }

    public static <V> Long getLong(String path, Map<String,V> map) {
        return (Long) get(path, map, new StringConverter());
    }
    public static <V> Long getLong(String path, Map<String,V> map, Long defaultValue) {
        Long original = (Long) get(path, map, new StringConverter());
        return original != null ? original : defaultValue;
    }
    public static <K,V> Long getLong(String path, Map<K,V> map, Converter<K> converter) {
        return (Long) get(path, map, converter);
    }

    public static <V> Float getFloat(String path, Map<String,V> map) {
        return (Float) get(path, map, new StringConverter());
    }
    public static <V> Float getFloat(String path, Map<String,V> map, Float defaultValue) {
        Float original = (Float) get(path, map, new StringConverter());
        return original != null ? original : defaultValue;
    }
    public static <K,V> Float getFloat(String path, Map<K,V> map, Converter<K> converter) {
        return (Float) get(path, map, converter);
    }

    public static <V> Double getDouble(String path, Map<String,V> map) {
        return (Double) get(path, map, new StringConverter());
    }
    public static <V> Double getDouble(String path, Map<String,V> map, Double defaultValue) {
        Double original = (Double) get(path, map, new StringConverter());
        return original != null ? original : defaultValue;
    }
    public static <K,V> Double getDouble(String path, Map<K,V> map, Converter<K> converter) {
        return (Double) get(path, map, converter);
    }

    public static <V> Boolean getBoolean(String path, Map<String,V> map) {
        return (Boolean) get(path, map, new StringConverter());
    }
    public static <V> Boolean getBoolean(String path, Map<String,V> map, Boolean defaultValue) {
        Boolean original = (Boolean) get(path, map, new StringConverter());
        return original != null ? original : defaultValue;
    }
    public static <K,V> Boolean getBoolean(String path, Map<K,V> map, Converter<K> converter) {
        return (Boolean) get(path, map, converter);
    }

    public static <V> Map<String,V> getMap(String path, Map<String,V> map) {
        return (Map<String, V>) get(path, map, new StringConverter());
    }
    public static <K,V> Map<K,V> getMap(String path, Map<K,V> map, Converter<K> converter) {
        return (Map<K, V>) get(path, map, converter);
    }

    public static <V> V get(String path, Map<String,V> map) {
        return get(path, map, new StringConverter());
    }

    /**
     * Concat the items of string array together
     * @param arr the array
     * @param start The beginning index, inclusive.
     * @param end The ending index, exclusive.
     * @param separator separator
     * @return
     */
    private static String join(String[] arr, int start, int end, String separator) {
        StringBuilder sb = new StringBuilder();

        for (int i = start; i < end; i++) {
            sb.append(arr[i]);
            sb.append(".");
        }

        return sb.substring(0,sb.length()-1);
    }

    private static String join(String[] arr, int start) {
        return join(arr, start, arr.length, ".");
    }
}
