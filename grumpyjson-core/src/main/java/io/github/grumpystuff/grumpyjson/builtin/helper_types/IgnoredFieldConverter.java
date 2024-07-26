/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.builtin.helper_types;

import io.github.grumpystuff.grumpyjson.JsonRegistries;
import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializationException;
import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializer;
import io.github.grumpystuff.grumpyjson.json_model.JsonElement;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializationException;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

/**
 * The converter for {@link IgnoredField}.
 * <p>
 * This converter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clear()}.
 */
public final class IgnoredFieldConverter implements JsonSerializer<IgnoredField>, JsonDeserializer {

    /**
     * Constructor.
     */
    public IgnoredFieldConverter() {
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        Objects.requireNonNull(type, "type");

        return type.equals(IgnoredField.class);
    }

    @Override
    public IgnoredField deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        return IgnoredField.INSTANCE;
    }

    @Override
    public IgnoredField deserializeAbsent(Type type) {
        Objects.requireNonNull(type, "type");

        return IgnoredField.INSTANCE;
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");

        return clazz.equals(IgnoredField.class);
    }

    @Override
    public JsonElement serialize(IgnoredField value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");

        throw new JsonSerializationException("found IgnoredField in a non-vanishable context");
    }

    @Override
    public Optional<JsonElement> serializeOptional(IgnoredField value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");

        return Optional.empty();
    }

}
