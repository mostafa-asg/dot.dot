package com.github.dotdot.converters;

public class FloatConverter implements Converter<Float> {
    public Float convert(String text) {
        return Float.valueOf(text);
    }
}
