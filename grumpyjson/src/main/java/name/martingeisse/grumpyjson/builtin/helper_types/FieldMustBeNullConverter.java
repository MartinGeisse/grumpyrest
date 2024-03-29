/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import name.martingeisse.grumpyjson.JsonRegistries;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * The converter for {@link FieldMustBeNull}.
 * <p>
 * This converter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clear()}.
 */
public final class FieldMustBeNullConverter implements JsonSerializer<FieldMustBeNull>, JsonDeserializer {

    /**
     * Constructor
     */
    public FieldMustBeNullConverter() {
        // needed to silence Javadoc error because the implicit constructor doesn't have a doc comment
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        return type.equals(FieldMustBeNull.class);
    }

    @Override
    public FieldMustBeNull deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        if (json instanceof JsonNull) {
            return FieldMustBeNull.INSTANCE;
        }
        throw new JsonDeserializationException("expected null, found: " + json);
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        return clazz.equals(FieldMustBeNull.class);
    }

    @Override
    public JsonElement serialize(FieldMustBeNull value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        return JsonNull.INSTANCE;
    }

}
