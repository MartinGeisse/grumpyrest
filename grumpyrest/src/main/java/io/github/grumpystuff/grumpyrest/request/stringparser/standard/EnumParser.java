/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest.request.stringparser.standard;

import io.github.grumpystuff.grumpyrest.request.stringparser.FromStringParser;
import io.github.grumpystuff.grumpyrest.request.stringparser.FromStringParserException;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A parser for enum types.
 *
 * @param <T> the enum type to parse
 */
public final class EnumParser<T extends Enum<T>> implements FromStringParser {

    private final Class<T> enumClass;

    /**
     * Constructor
     *
     * @param enumClass the enum class to parse
     */
    public EnumParser(Class<T> enumClass) {
        Objects.requireNonNull(enumClass, "enumClass");

        this.enumClass = enumClass;
    }

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");

        return type.equals(enumClass);
    }

    @Override
    public Object parseFromString(String s, Type type) throws FromStringParserException {
        Objects.requireNonNull(s, "s");
        Objects.requireNonNull(type, "type");

        try {
            return Enum.valueOf(enumClass, s);
        } catch (IllegalArgumentException e) {
            throw new FromStringParserException("unknown value: " + s);
        }
    }

}
