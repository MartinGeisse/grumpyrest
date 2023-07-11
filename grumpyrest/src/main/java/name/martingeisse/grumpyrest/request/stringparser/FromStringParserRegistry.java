/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.stringparser;

import name.martingeisse.grumpyjson.registry.NotRegisteredException;
import name.martingeisse.grumpyjson.registry.Registry;
import name.martingeisse.grumpyrest.RestApi;

import java.lang.reflect.Type;

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
    protected FromStringParser generateRegistrable(Type key) {
        return null;
    }

    @Override
    protected String getErrorMessageForUnknownKey(Type key) {
        return "no from-string parser found for type: " + key;
    }

    @Override
    protected boolean registrableSupports(FromStringParser registrable, Type key) {
        return registrable.supportsType(key);
    }

    @Override
    public Object parseFromString(String text, Type type) throws FromStringParserException {
        FromStringParser parser;
        try {
            parser = get(type);
        } catch (NotRegisteredException e) {
            throw new FromStringParserException("no parser registered for type " + type);
        }
        return parser.parseFromString(text, type);
    }

}
