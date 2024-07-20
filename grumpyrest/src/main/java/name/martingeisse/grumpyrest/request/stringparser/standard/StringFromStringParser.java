/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.stringparser.standard;

import name.martingeisse.grumpyrest.request.stringparser.FromStringParser;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Returns the argument text as a {@link String} without any actual parsing.
 */
public final class StringFromStringParser implements FromStringParser {

    /**
     * Constructor.
     */
    public StringFromStringParser() {
    }

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");

        return type.equals(String.class);
    }

    @Override
    public Object parseFromString(String s, Type type) {
        Objects.requireNonNull(s, "s");
        Objects.requireNonNull(type, "type");

        return s;
    }

}
