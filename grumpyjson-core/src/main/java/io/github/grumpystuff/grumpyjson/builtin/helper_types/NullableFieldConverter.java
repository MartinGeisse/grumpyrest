/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.builtin.helper_types;

import io.github.grumpystuff.grumpyjson.JsonProviders;
import io.github.grumpystuff.grumpyjson.JsonRegistries;
import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializationException;
import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializer;
import io.github.grumpystuff.grumpyjson.json_model.JsonElement;
import io.github.grumpystuff.grumpyjson.json_model.JsonNull;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializationException;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializer;
import io.github.grumpystuff.grumpyjson.util.TypeUtil;

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
        if (json instanceof JsonNull) {
            return NullableField.ofNull();
        } else {
            try {
                return NullableField.ofValue(providers.deserialize(json, innerType));
            } catch (JsonDeserializationException e) {
                throw e;
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
            } catch (JsonSerializationException e) {
                throw e;
            } catch (Exception e) {
                throw new JsonSerializationException(e);
            }
        }
    }

}
