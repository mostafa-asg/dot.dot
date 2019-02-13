package com.github.dotdot.converters;

public class BooleanConverter implements Converter<Boolean> {
    public Boolean convert(String text) {
        if (text.equals("1") || text.equalsIgnoreCase("true")) {
            return true;
        }

        return false;
    }
}
