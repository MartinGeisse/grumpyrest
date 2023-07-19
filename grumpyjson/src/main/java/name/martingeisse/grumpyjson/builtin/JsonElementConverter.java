/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.JsonRegistries;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public final class JsonElementConverter implements JsonSerializer<JsonElement>, JsonDeserializer {

    /**
     * Constructor
     */
    public JsonElementConverter() {
        // needed to silence Javadoc error because the implicit constructor doesn't have a doc comment
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        Objects.requireNonNull(type, "type");
        return (type instanceof Class<?> clazz) && JsonElement.class.isAssignableFrom(clazz);
    }

    @Override
    public JsonElement deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        return json.deepCopy();
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");
        return JsonElement.class.isAssignableFrom(clazz);
    }

    @Override
    public JsonElement serialize(JsonElement value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        return value.deepCopy();
    }

}
