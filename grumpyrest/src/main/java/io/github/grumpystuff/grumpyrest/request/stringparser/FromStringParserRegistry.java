/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest.request.stringparser;

import io.github.grumpystuff.grumpyjson.registry.NotRegisteredException;
import io.github.grumpystuff.grumpyjson.registry.Registry;
import io.github.grumpystuff.grumpyrest.RestApi;
import io.github.grumpystuff.grumpyrest.request.stringparser.standard.EnumParser;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * This registry keeps {@link FromStringParser}s used to parse path parameters and querystring parameters.
 */
public final class FromStringParserRegistry extends Registry<Type, FromStringParser> implements ParseFromStringService {

    /**
     * Constructor. This constructor does not register any standard parsers, but the {@link RestApi} calling this
     * constructor does.
     */
    public FromStringParserRegistry() {
    }

    @Override
    protected FromStringParser generateRegistrable(Type type) {
        Objects.requireNonNull(type, "type");

        //noinspection rawtypes
        if (type instanceof Class clazz && clazz.isEnum()) {
            //noinspection unchecked,rawtypes
            return new EnumParser(clazz);
        }
        return null;
    }

    @Override
    protected String getErrorMessageForUnknownKey(Type key) {
        Objects.requireNonNull(key, "key");

        return "no from-string parser found for type: " + key;
    }

    @Override
    protected boolean registrableSupports(FromStringParser registrable, Type key) {
        Objects.requireNonNull(registrable, "registrable");
        Objects.requireNonNull(key, "key");

        return registrable.supportsType(key);
    }

    @Override
    public Object parseFromString(String text, Type type) throws FromStringParserException {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(type, "type");

        FromStringParser parser;
        try {
            parser = get(type);
        } catch (NotRegisteredException e) {
            throw new FromStringParserException("no parser registered for type " + type);
        }
        return parser.parseFromString(text, type);
    }

}
