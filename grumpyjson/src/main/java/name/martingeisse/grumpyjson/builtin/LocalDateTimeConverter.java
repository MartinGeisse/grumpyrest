/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import name.martingeisse.grumpyjson.JsonRegistries;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * A converter for {@link LocalDateTime}.
 * <p>
 * This converter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clear()}.
 */
public final class LocalDateTimeConverter implements JsonSerializer<LocalDateTime>, JsonDeserializer {

    /**
     * Constructor
     */
    public LocalDateTimeConverter() {
        // needed to silence Javadoc error because the implicit constructor doesn't have a doc comment
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        Objects.requireNonNull(type, "type");

        return type.equals(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        if (json instanceof JsonPrimitive primitive && primitive.isString()) {
            try {
                return LocalDateTime.parse(primitive.getAsString());
            } catch (DateTimeParseException e) {
                throw new JsonDeserializationException(e.getMessage());
            }
        }
        throw new JsonDeserializationException("expected string, found: " + json);
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");

        return clazz.equals(LocalDateTime.class);
    }

    @Override
    public JsonElement serialize(LocalDateTime value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");

        return new JsonPrimitive(value.toString());
    }

}
