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
