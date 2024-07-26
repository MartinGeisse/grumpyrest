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
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * A parser for {@link LocalTime}.
 */
public final class LocalTimeParser implements FromStringParser {

    /**
     * Constructor
     */
    public LocalTimeParser() {
        // needed to silence Javadoc error because the implicit constructor doesn't have a doc comment
    }

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");

        return type.equals(LocalTime.class);
    }

    @Override
    public Object parseFromString(String s, Type type) throws FromStringParserException {
        Objects.requireNonNull(s, "s");
        Objects.requireNonNull(type, "type");

        try {
            return LocalTime.parse(s);
        } catch (DateTimeParseException e) {
            throw new FromStringParserException(e.getMessage());
        }
    }

}
