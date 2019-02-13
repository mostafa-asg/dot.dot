package com.github.dotdot.converters;

public class ShortConverter implements Converter<Short> {
    public Short convert(String text) {
        return Short.valueOf(text);
    }
}
