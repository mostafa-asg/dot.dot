package com.github.dotdot;

import com.github.dotdot.converters.Converter;
import com.github.dotdot.converters.StringConverter;

import java.util.Map;

public class DotDot {

    /**
     * Returns the value of last key
     * Keys should separated by dot in the format of keyLevel1.keyLevel2.keyLevelK
     * In this case if all keys have value then it will return the value of last key 'keyLevelK'
     * Otherwise return null.
     * @param path nested keys that separated by dot(.)
     * @param map your key/value map
     * @param converter converter for converting string representation to a your key type
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

    public static <V> String getString(String path, Map<String,V> map) {
        return (String) get(path, map, new StringConverter());
    }
    public static <K,V> String getString(String path, Map<K,V> map, Converter<K> converter) {
        return (String) get(path, map, converter);
    }

    public static <V> Character getChar(String path, Map<String,V> map) {
        return (Character) get(path, map, new StringConverter());
    }
    public static <K,V> Character getChar(String path, Map<K,V> map, Converter<K> converter) {
        return (Character) get(path, map, converter);
    }

    public static <V> Byte getByte(String path, Map<String,V> map) {
        return (Byte) get(path, map, new StringConverter());
    }
    public static <K,V> Byte getByte(String path, Map<K,V> map, Converter<K> converter) {
        return (Byte) get(path, map, converter);
    }

    public static <V> Short getShort(String path, Map<String,V> map) {
        return (Short) get(path, map, new StringConverter());
    }
    public static <K,V> Short getShort(String path, Map<K,V> map, Converter<K> converter) {
        return (Short) get(path, map, converter);
    }

    public static <V> Integer getInt(String path, Map<String,V> map) {
        return (Integer) get(path, map, new StringConverter());
    }
    public static <K,V> Integer getInt(String path, Map<K,V> map, Converter<K> converter) {
        return (Integer) get(path, map, converter);
    }

    public static <V> Long getLong(String path, Map<String,V> map) {
        return (Long) get(path, map, new StringConverter());
    }
    public static <K,V> Long getLong(String path, Map<K,V> map, Converter<K> converter) {
        return (Long) get(path, map, converter);
    }

    public static <V> Float getFloat(String path, Map<String,V> map) {
        return (Float) get(path, map, new StringConverter());
    }
    public static <K,V> Float getFloat(String path, Map<K,V> map, Converter<K> converter) {
        return (Float) get(path, map, converter);
    }

    public static <V> Double getDouble(String path, Map<String,V> map) {
        return (Double) get(path, map, new StringConverter());
    }
    public static <K,V> Double getDouble(String path, Map<K,V> map, Converter<K> converter) {
        return (Double) get(path, map, converter);
    }

    public static <V> Boolean getBoolean(String path, Map<String,V> map) {
        return (Boolean) get(path, map, new StringConverter());
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
