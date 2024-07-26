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

/**
 * Parses longs from their usual decimal text representation.
 */
public final class LongFromStringParser implements FromStringParser {

    /**
     * Constructor.
     */
    public LongFromStringParser() {
    }

    @Override
    public boolean supportsType(Type type) {
        return type.equals(Long.TYPE) || type.equals(Long.class);
    }

    @Override
    public Object parseFromString(String s, Type type) throws FromStringParserException {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new FromStringParserException("expected long");
        }
    }

}
