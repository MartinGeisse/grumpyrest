/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.stringparser.standard;

import name.martingeisse.grumpyjson.builtin.helper_types.OptionalField;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParser;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParserException;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParserRegistry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Wraps another parser to make a field optional, returning an {@link OptionalField} whih wraps the pared value (or
 * nothing of the argument to parse is absent).
 */
public final class OptionalFieldParser implements FromStringParser {

    private final FromStringParserRegistry registry;

    /**
     * Constructor.
     */
    public OptionalFieldParser(FromStringParserRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean supportsType(Type type) {
        return (type instanceof ParameterizedType p) && (p.getRawType().equals(OptionalField.class));
    }

    @Override
    public Object parseFromString(String s, Type type) throws FromStringParserException {
        Type innerType = ((ParameterizedType) type).getActualTypeArguments()[0];
        return OptionalField.ofValue(registry.parseFromString(s, innerType));
    }

    @Override
    public Object parseFromAbsentString(Type type) {
        return OptionalField.ofNothing();
    }

}
