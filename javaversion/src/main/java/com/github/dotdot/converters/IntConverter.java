package com.github.dotdot.converters;

public class IntConverter implements Converter<Integer> {
    public Integer convert(String text) {
        return Integer.valueOf(text);
    }
}
