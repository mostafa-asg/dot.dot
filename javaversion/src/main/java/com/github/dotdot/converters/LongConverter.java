package com.github.dotdot.converters;

public class LongConverter implements Converter<Long> {
    public Long convert(String text) {
        return Long.valueOf(text);
    }
}
