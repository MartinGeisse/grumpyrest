/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.deserialize;

import io.github.grumpystuff.grumpyjson.JsonEngine;
import io.github.grumpystuff.grumpyjson.builtin.EnumConverter;
import io.github.grumpystuff.grumpyjson.builtin.record.RecordConverterFactory;
import io.github.grumpystuff.grumpyjson.registry.NotRegisteredException;
import io.github.grumpystuff.grumpyjson.registry.Registry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * This registry keeps the {@link JsonDeserializer}s used by a {@link JsonEngine}.
 */
public final class JsonDeserializerRegistry extends Registry<Type, JsonDeserializer> implements JsonDeserializerProvider {

    private final RecordConverterFactory recordConverterFactory;

    /**
     * NOT PUBLIC API
     *
     * @param recordConverterFactory ...
     */
    public JsonDeserializerRegistry(RecordConverterFactory recordConverterFactory) {
        Objects.requireNonNull(recordConverterFactory, "recordConverterFactory");

        this.recordConverterFactory = recordConverterFactory;
    }

    @Override
    protected boolean registrableSupports(JsonDeserializer registrable, Type key) {
        Objects.requireNonNull(registrable, "registrable");
        Objects.requireNonNull(key, "key");

        return registrable.supportsTypeForDeserialization(key);
    }

    @Override
    protected JsonDeserializer generateRegistrable(Type type) {
        Objects.requireNonNull(type, "type");

        Class<?> rawClass;
        if (type instanceof Class<?> c) {
            rawClass = c;
        } else if (type instanceof ParameterizedType p && p.getRawType() instanceof Class<?> c) {
            rawClass = c;
        } else {
            return null;
        }
        if (rawClass.isRecord()) {
            return recordConverterFactory.getDeserializer(rawClass);
        }
        if (rawClass.isEnum()) {
            //noinspection unchecked,rawtypes
            return new EnumConverter(rawClass);
        }
        return null;
    }

    @Override
    protected String getErrorMessageForUnknownKey(Type type) {
        Objects.requireNonNull(type, "type");

        return "no JSON deserializer found and can only auto-generate them for record types, found type: " + type;
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        Objects.requireNonNull(type, "type");

        return supports(type);
    }

    @Override
    public JsonDeserializer getDeserializer(Type type) throws NotRegisteredException {
        Objects.requireNonNull(type, "type");

        return get(type);
    }
}
