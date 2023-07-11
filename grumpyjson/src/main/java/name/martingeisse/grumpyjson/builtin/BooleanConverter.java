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
import name.martingeisse.grumpyjson.JsonTypeAdapter;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A converter for the primitive type boolean and its boxed type, {@link Boolean}.
 * <p>
 * This maps to and from JSON boolean values.
 * <p>
 * This converter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clear()}.
 */
public class BooleanConverter implements JsonTypeAdapter<Boolean> {

    /**
     * Constructor
     */
    public BooleanConverter() {
        // needed to silence Javadoc error because the implicit constructor doesn't have a doc comment
    }

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        return type.equals(Boolean.TYPE) || type.equals(Boolean.class);
    }

    @Override
    public Boolean deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        if (json instanceof JsonPrimitive primitive) {
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }
        }

        throw new JsonDeserializationException("expected boolean, found: " + json);
    }

    @Override
    public JsonElement serialize(Boolean value, Type type) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        return new JsonPrimitive(value);
    }

}
