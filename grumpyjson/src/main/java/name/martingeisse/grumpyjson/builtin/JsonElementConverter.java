/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.JsonRegistry;
import name.martingeisse.grumpyjson.JsonTypeAdapter;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A {@link JsonTypeAdapter} for {@link JsonElement}.
 * <p>
 * This adapter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistry#clearTypeAdapters()}.
 */
public final class JsonElementConverter implements JsonTypeAdapter<JsonElement> {

    /**
     * Constructor
     */
    public JsonElementConverter() {
        // needed to silence Javadoc error because the implicit constructor doesn't have a doc comment
    }

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        return (type instanceof Class<?> clazz) && JsonElement.class.isAssignableFrom(clazz);
    }

    @Override
    public JsonElement deserialize(JsonElement json, Type type) {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        return json.deepCopy();
    }

    @Override
    public JsonElement serialize(JsonElement value, Type type) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        return value.deepCopy();
    }

}
