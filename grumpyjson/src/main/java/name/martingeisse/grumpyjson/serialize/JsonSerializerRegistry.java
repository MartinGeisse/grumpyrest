/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.serialize;

import name.martingeisse.grumpyjson.JsonEngine;
import name.martingeisse.grumpyjson.builtin.EnumConverter;
import name.martingeisse.grumpyjson.builtin.record.RecordConverterFactory;
import name.martingeisse.grumpyjson.registry.NotRegisteredException;
import name.martingeisse.grumpyjson.registry.Registry;

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
        this.recordConverterFactory = recordConverterFactory;
    }

    @Override
    protected boolean registrableSupports(JsonSerializer<?> registrable, Class<?> key) {
        return registrable.supportsClassForSerialization(key);
    }

    @Override
    protected JsonSerializer<?> generateRegistrable(Class<?> clazz) {
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
        return "no JSON serializer found and can only auto-generate them for record classes, found class: " + clazz;
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        return supports(clazz);
    }

    @Override
    public <T> JsonSerializer<T> getSerializer(Class<T> clazz) throws NotRegisteredException {
        //noinspection unchecked
        return (JsonSerializer<T>)get(clazz);
    }

}
