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
 * A converter for the primitive type int and its boxed type, {@link Integer}.
 * <p>
 * This maps to and from integral JSON numbers in the 32-bit signed integer range.
 * <p>
 * This converter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clearTypeAdapters()}.
 */
public class IntegerConverter implements JsonTypeAdapter<Integer> {

    /**
     * Constructor
     */
    public IntegerConverter() {
        // needed to silence Javadoc error because the implicit constructor doesn't have a doc comment
    }

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        return type.equals(Integer.TYPE) || type.equals(Integer.class);
    }

    @Override
    public Integer deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        if (json instanceof JsonPrimitive primitive) {
            if (primitive.isNumber()) {
                long longValue = primitive.getAsLong();
                // test for float
                if (primitive.equals(new JsonPrimitive(longValue))) {
                    // test for too-large-for-int
                    int intValue = (int)longValue;
                    if (longValue != (long)intValue) {
                        throw new JsonDeserializationException("value out of bounds: " + longValue);
                    }
                    return intValue;
                }
            }
        }

        throw new JsonDeserializationException("expected integer, found: " + json);
    }

    @Override
    public JsonElement serialize(Integer value, Type type) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        return new JsonPrimitive(value);
    }

}
