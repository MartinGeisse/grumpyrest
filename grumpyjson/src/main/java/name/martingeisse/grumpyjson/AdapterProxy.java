/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Represents a {@link JsonTypeAdapter} in the registry while the type adapter is being built, to support cyclic types.
 *
 * @param <T> the payload type of the adapter being built
 */
final class AdapterProxy<T> implements JsonTypeAdapter<T> {

    private JsonTypeAdapter<T> target;

    public void setTarget(JsonTypeAdapter<T> target) {
        this.target = Objects.requireNonNull(target);
    }

    private JsonTypeAdapter<T> needTarget() {
        if (target == null) {
            throw new IllegalStateException("using an AdapterProxy before its target has been set");
        }
        return target;
    }

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        return needTarget().supportsType(type);
    }

    @Override
    public T deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        return needTarget().deserialize(json, type);
    }

    @Override
    public JsonElement serialize(T value, Type type) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        return needTarget().serialize(value, type);
    }
}
