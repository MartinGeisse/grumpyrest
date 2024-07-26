/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.builtin;

import io.github.grumpystuff.grumpyjson.JsonRegistries;
import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializationException;
import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializer;
import io.github.grumpystuff.grumpyjson.json_model.JsonElement;
import io.github.grumpystuff.grumpyjson.json_model.JsonString;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializationException;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A converter for type {@link String}.
 * <p>
 * This maps to and from JSON strings. No other mapping exists; in particular, JSON numbers are not converted to
 * strings by this converter.
 * <p>
 * This converter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clear()}.
 */
public final class StringConverter implements JsonSerializer<String>, JsonDeserializer {

    /**
     * Constructor
     */
    public StringConverter() {
        // needed to silence Javadoc error because the implicit constructor doesn't have a doc comment
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        Objects.requireNonNull(type, "type");

        return type.equals(String.class);
    }

    @Override
    public String deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        return json.deserializerExpectsString();
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");

        return clazz.equals(String.class);
    }

    @Override
    public JsonElement serialize(String value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");

        return JsonString.of(value);
    }

}
