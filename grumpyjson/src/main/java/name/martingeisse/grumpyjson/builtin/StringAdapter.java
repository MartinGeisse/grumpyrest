/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import name.martingeisse.grumpyjson.JsonRegistry;
import name.martingeisse.grumpyjson.JsonTypeAdapter;
import name.martingeisse.grumpyjson.JsonValidationException;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A {@link JsonTypeAdapter} for type {@link String}.
 * <p>
 * This maps to and from JSON strings. No other mapping exists; in particular, JSON numbers are not converted to
 * strings by this adapter.
 * <p>
 * This adapter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistry#clearTypeAdapters()}.
 */
public class StringAdapter implements JsonTypeAdapter<String> {

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        return type.equals(String.class);
    }

    @Override
    public String fromJson(JsonElement json, Type type) throws JsonValidationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        if (json instanceof JsonPrimitive primitive) {
            if (primitive.isString()) {
                return primitive.getAsString();
            }
        }
        throw new JsonValidationException("expected int, found: " + json);
    }

    @Override
    public JsonElement toJson(String value, Type type) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        return new JsonPrimitive(value);
    }

}
