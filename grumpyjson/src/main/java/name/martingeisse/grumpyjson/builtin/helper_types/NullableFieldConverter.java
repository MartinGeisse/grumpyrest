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
import name.martingeisse.grumpyjson.util.TypeUtil;

import java.lang.reflect.Type;

/**
 * The converter for {@link NullableField}.
 * <p>
 * This converter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clear()}.
 */
public final class NullableFieldConverter implements JsonSerializer<NullableField<?>>, JsonDeserializer {

    private final JsonRegistries registries;

    /**
     * Constructor.
     *
     * @param registries needed to fetch the converter for the contained type at run-time
     */
    public NullableFieldConverter(JsonRegistries registries) {
        this.registries = registries;
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        return TypeUtil.isSingleParameterizedType(type, NullableField.class) != null;
    }

    @Override
    public NullableField<?> deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Type innerType = TypeUtil.expectSingleParameterizedType(type, NullableField.class);
        if (json.isJsonNull()) {
            return NullableField.ofNull();
        } else {
            try {
                return NullableField.ofValue(registries.deserialize(json, innerType));
            } catch (Exception e) {
                throw new JsonDeserializationException(e);
            }
        }
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        return clazz.equals(NullableField.class);
    }

    @Override
    public JsonElement serialize(NullableField<?> value) throws JsonSerializationException {
        if (value.isNull()) {
            return JsonNull.INSTANCE;
        } else {
            try {
                return registries.serialize(value.getValueOrNull());
            } catch (Exception e) {
                throw new JsonSerializationException(e);
            }
        }
    }

}
