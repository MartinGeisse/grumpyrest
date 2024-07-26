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
 * Parses integers from their usual decimal text representation.
 */
public final class IntegerFromStringParser implements FromStringParser {

    /**
     * Constructor.
     */
    public IntegerFromStringParser() {
    }

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");

        return type.equals(Integer.TYPE) || type.equals(Integer.class);
    }

    @Override
    public Object parseFromString(String s, Type type) throws FromStringParserException {
        Objects.requireNonNull(s, "s");
        Objects.requireNonNull(type, "type");

        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new FromStringParserException("expected integer");
        }
    }

}
