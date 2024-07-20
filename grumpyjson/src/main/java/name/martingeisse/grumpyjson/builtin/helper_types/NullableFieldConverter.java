/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import name.martingeisse.grumpyjson.JsonProviders;
import name.martingeisse.grumpyjson.JsonRegistries;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;
import name.martingeisse.grumpyjson.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * The converter for {@link NullableField}.
 * <p>
 * This converter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clear()}.
 */
public final class NullableFieldConverter implements JsonSerializer<NullableField<?>>, JsonDeserializer {

    private final JsonProviders providers;

    /**
     * Constructor.
     *
     * @param providers needed to fetch the converter for the contained type at run-time
     */
    public NullableFieldConverter(JsonProviders providers) {
        Objects.requireNonNull(providers, "providers");

        this.providers = providers;
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        Objects.requireNonNull(type, "type");

        return TypeUtil.isSingleParameterizedType(type, NullableField.class) != null;
    }

    @Override
    public NullableField<?> deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        Type innerType = TypeUtil.expectSingleParameterizedType(type, NullableField.class);
        if (json.isJsonNull()) {
            return NullableField.ofNull();
        } else {
            try {
                return NullableField.ofValue(providers.deserialize(json, innerType));
            } catch (Exception e) {
                throw new JsonDeserializationException(e);
            }
        }
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");

        return clazz.equals(NullableField.class);
    }

    @Override
    public JsonElement serialize(NullableField<?> value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");

        if (value.isNull()) {
            return JsonNull.INSTANCE;
        } else {
            try {
                return providers.serialize(value.getValueOrNull());
            } catch (Exception e) {
                throw new JsonSerializationException(e);
            }
        }
    }

}
