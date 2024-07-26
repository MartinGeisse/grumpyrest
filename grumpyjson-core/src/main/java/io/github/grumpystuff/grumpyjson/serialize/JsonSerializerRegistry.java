/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.serialize;

import io.github.grumpystuff.grumpyjson.JsonEngine;
import io.github.grumpystuff.grumpyjson.builtin.EnumConverter;
import io.github.grumpystuff.grumpyjson.builtin.record.RecordConverterFactory;
import io.github.grumpystuff.grumpyjson.registry.NotRegisteredException;
import io.github.grumpystuff.grumpyjson.registry.Registry;

import java.util.Objects;

/**
 * This registry keeps the {@link JsonSerializer}s used by a {@link JsonEngine}.
 */
public final class JsonSerializerRegistry extends Registry<Class<?>, JsonSerializer<?>> implements JsonSerializerProvider {

    private final RecordConverterFactory recordConverterFactory;

    /**
     * NOT PUBLIC API
     *
     * @param recordConverterFactory ...
     */
    public JsonSerializerRegistry(RecordConverterFactory recordConverterFactory) {
        Objects.requireNonNull(recordConverterFactory, "recordConverterFactory");

        this.recordConverterFactory = recordConverterFactory;
    }

    @Override
    protected boolean registrableSupports(JsonSerializer<?> registrable, Class<?> key) {
        Objects.requireNonNull(registrable, "registrable");
        Objects.requireNonNull(key, "key");

        return registrable.supportsClassForSerialization(key);
    }

    @Override
    protected JsonSerializer<?> generateRegistrable(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");

        if (clazz.isRecord()) {
            return recordConverterFactory.getSerializer(clazz);
        }
        if (clazz.isEnum()) {
            //noinspection unchecked,rawtypes
            return new EnumConverter(clazz);
        }
        return null;
    }

    @Override
    protected String getErrorMessageForUnknownKey(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");

        return "no JSON serializer found and can only auto-generate them for record classes, found class: " + clazz;
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");

        return supports(clazz);
    }

    @Override
    public <T> JsonSerializer<T> getSerializer(Class<T> clazz) throws NotRegisteredException {
        Objects.requireNonNull(clazz, "clazz");

        //noinspection unchecked
        return (JsonSerializer<T>)get(clazz);
    }

}
