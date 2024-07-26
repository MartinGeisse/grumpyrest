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
import name.martingeisse.grumpyjson.json_model.JsonNumber;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A converter for the primitive type long and its boxed type, {@link Long}.
 * <p>
 * This maps to and from integral JSON numbers in the 64-bit signed integer range.
 * <p>
 * This converter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clear()}.
 */
public final class LongConverter implements JsonSerializer<Long>, JsonDeserializer {

    /**
     * Constructor
     */
    public LongConverter() {
        // needed to silence Javadoc error because the implicit constructor doesn't have a doc comment
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        Objects.requireNonNull(type, "type");

        return type.equals(Long.TYPE) || type.equals(Long.class);
    }

    @Override
    public Long deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        return IntegralNumberDeserializationUtil.deserialize(json.deserializerExpectsNumber());
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");

        return clazz.equals(Long.TYPE) || clazz.equals(Long.class);
    }

    @Override
    public JsonElement serialize(Long value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");

        return JsonNumber.of(value);
    }

}
