/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import name.martingeisse.grumpyjson.JsonRegistries;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.json_model.JsonElement;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;

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
