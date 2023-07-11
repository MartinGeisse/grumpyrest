/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import name.martingeisse.grumpyjson.*;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.util.TypeUtil;

import java.lang.reflect.Type;

/**
 * The {@link JsonTypeAdapter} for {@link NullableField}.
 * <p>
 * This adapter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clearTypeAdapters()}.
 */
public class NullableFieldConverter implements JsonTypeAdapter<NullableField<?>> {

    private final JsonRegistries registry;

    /**
     * Constructor.
     *
     * @param registry the JSON registry -- needed to fetch the adapter for the contained type at run-time
     */
    public NullableFieldConverter(JsonRegistries registry) {
        this.registry = registry;
    }

    @Override
    public boolean supportsType(Type type) {
        return TypeUtil.isSingleParameterizedType(type, NullableField.class) != null;
    }

    @Override
    public NullableField<?> deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Type innerType = TypeUtil.expectSingleParameterizedType(type, NullableField.class);
        if (json.isJsonNull()) {
            return NullableField.ofNull();
        }
        JsonTypeAdapter<?> innerAdapter = registry.getTypeAdapter(innerType);
        try {
            return NullableField.ofValue(innerAdapter.deserialize(json, innerType));
        } catch (JsonDeserializationException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonDeserializationException(e);
        }
    }

    @Override
    public JsonElement serialize(NullableField<?> value, Type type) throws JsonSerializationException {
        Type innerType = TypeUtil.expectSingleParameterizedType(type, NullableField.class);
        if (value.isNull()) {
            return JsonNull.INSTANCE;
        }
        @SuppressWarnings("rawtypes") JsonTypeAdapter innerAdapter = registry.getTypeAdapter(innerType);
        try {
            //noinspection unchecked
            return innerAdapter.serialize(value.getValueOrNull(), innerType);
        } catch (JsonSerializationException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonSerializationException(e);
        }
    }

}
