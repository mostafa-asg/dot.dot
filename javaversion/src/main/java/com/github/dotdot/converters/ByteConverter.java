package com.github.dotdot.converters;

public class ByteConverter implements Converter<Byte> {
    public Byte convert(String text) {
        return Byte.valueOf(text);
    }
}
