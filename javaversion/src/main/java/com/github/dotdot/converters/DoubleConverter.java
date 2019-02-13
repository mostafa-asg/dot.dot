package com.github.dotdot.converters;

public class DoubleConverter implements Converter<Double> {
    public Double convert(String text) {
        return Double.valueOf(text);
    }
}
