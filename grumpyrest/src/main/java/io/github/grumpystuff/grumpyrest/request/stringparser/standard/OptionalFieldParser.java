/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest.request.stringparser.standard;

import io.github.grumpystuff.grumpyjson.builtin.helper_types.OptionalField;
import io.github.grumpystuff.grumpyrest.request.stringparser.FromStringParser;
import io.github.grumpystuff.grumpyrest.request.stringparser.FromStringParserException;
import io.github.grumpystuff.grumpyrest.request.stringparser.FromStringParserRegistry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Parses an optional field, returning an {@link OptionalField} which wraps the parsed value (or nothing if the
 * field to parse is absent). The type argument fo the <code>OptionalField</code> is used to select the actual parser
 * if the field is present.
 */
public final class OptionalFieldParser implements FromStringParser {

    private final FromStringParserRegistry registry;

    /**
     * Constructor.
     *
     * @param registry needed to fetch the parser for the contained type at run-time
     */
    public OptionalFieldParser(FromStringParserRegistry registry) {
        Objects.requireNonNull(registry, "registry");

        this.registry = registry;
    }

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");

        return (type instanceof ParameterizedType p) && (p.getRawType().equals(OptionalField.class));
    }

    @Override
    public Object parseFromString(String s, Type type) throws FromStringParserException {
        Objects.requireNonNull(s, "s");
        Objects.requireNonNull(type, "type");

        Type innerType = ((ParameterizedType) type).getActualTypeArguments()[0];
        return OptionalField.ofValue(registry.parseFromString(s, innerType));
    }

    @Override
    public Object parseFromAbsentString(Type type) {
        Objects.requireNonNull(type, "type");

        return OptionalField.ofNothing();
    }

}
