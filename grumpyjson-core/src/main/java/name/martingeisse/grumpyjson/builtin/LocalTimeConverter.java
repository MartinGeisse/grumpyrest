/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import name.martingeisse.grumpyjson.JsonRegistries;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.json_model.JsonElement;
import name.martingeisse.grumpyjson.json_model.JsonString;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * A converter for {@link LocalTime}.
 * <p>
 * This converter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clear()}.
 */
public final class LocalTimeConverter implements JsonSerializer<LocalTime>, JsonDeserializer {

    /**
     * Constructor
     */
    public LocalTimeConverter() {
        // needed to silence Javadoc error because the implicit constructor doesn't have a doc comment
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        Objects.requireNonNull(type, "type");

        return type.equals(LocalTime.class);
    }

    @Override
    public LocalTime deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        String text = json.deserializerExpectsString();
        try {
            return LocalTime.parse(text);
        } catch (DateTimeParseException e) {
            throw new JsonDeserializationException(e.getMessage());
        }
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");

        return clazz.equals(LocalTime.class);
    }

    @Override
    public JsonElement serialize(LocalTime value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");

        return JsonString.of(value.toString());
    }

}
