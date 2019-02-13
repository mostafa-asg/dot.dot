package com.github.dotdot.converters;

public class CharacterConverter implements Converter<Character> {
    public Character convert(String text) {
        return text.charAt(0);
    }
}
