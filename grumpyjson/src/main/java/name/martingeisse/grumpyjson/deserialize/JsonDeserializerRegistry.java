/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.deserialize;

import name.martingeisse.grumpyjson.JsonEngine;
import name.martingeisse.grumpyjson.builtin.record.RecordConverterFactory;
import name.martingeisse.grumpyjson.registry.NotRegisteredException;
import name.martingeisse.grumpyjson.registry.Registry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * This registry keeps the {@link JsonDeserializer}s used by a {@link JsonEngine}.
 */
public final class JsonDeserializerRegistry extends Registry<Type, JsonDeserializer> implements JsonDeserializerProvider {

    private final RecordConverterFactory recordConverterFactory;

    /**
     * Constructor
     */
    public JsonDeserializerRegistry(RecordConverterFactory recordConverterFactory) {
        this.recordConverterFactory = recordConverterFactory;
    }

    @Override
    protected boolean registrableSupports(JsonDeserializer registrable, Type key) {
        return registrable.supportsTypeForDeserialization(key);
    }

    @Override
    protected JsonDeserializer generateRegistrable(Type type) {
        Class<?> rawClass;
        if (type instanceof Class<?> c) {
            rawClass = c;
        } else if (type instanceof ParameterizedType p && p.getRawType() instanceof Class<?> c) {
            rawClass = c;
        } else {
            return null;
        }
        if (!rawClass.isRecord()) {
            return null;
        }
        return recordConverterFactory.getDeserializer(rawClass);
    }

    @Override
    protected String getErrorMessageForUnknownKey(Type type) {
        return "no JSON deserializer found and can only auto-generate them for record types, found type: " + type;
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        return supports(type);
    }

    @Override
    public JsonDeserializer getDeserializer(Type type) throws NotRegisteredException {
        return get(type);
    }
}
