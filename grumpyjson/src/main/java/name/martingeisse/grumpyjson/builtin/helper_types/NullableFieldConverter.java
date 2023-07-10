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
import name.martingeisse.grumpyjson.util.TypeUtil;

import java.lang.reflect.Type;

/**
 * The {@link JsonTypeAdapter} for {@link NullableField}.
 * <p>
 * This adapter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistry#clearTypeAdapters()}.
 */
public class NullableFieldConverter implements JsonTypeAdapter<NullableField<?>> {

    private final JsonRegistry registry;

    /**
     * Constructor.
     *
     * @param registry the JSON registry -- needed to fetch the adapter for the contained type at run-time
     */
    public NullableFieldConverter(JsonRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean supportsType(Type type) {
        return TypeUtil.isSingleParameterizedType(type, NullableField.class) != null;
    }

    @Override
    public NullableField<?> fromJson(JsonElement json, Type type) throws JsonValidationException {
        Type innerType = TypeUtil.expectSingleParameterizedType(type, NullableField.class);
        if (json.isJsonNull()) {
            return NullableField.ofNull();
        }
        JsonTypeAdapter<?> innerAdapter = registry.getTypeAdapter(innerType);
        try {
            return NullableField.ofValue(innerAdapter.fromJson(json, innerType));
        } catch (JsonValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonValidationException(e);
        }
    }

    @Override
    public JsonElement toJson(NullableField<?> value, Type type) throws JsonGenerationException {
        Type innerType = TypeUtil.expectSingleParameterizedType(type, NullableField.class);
        if (value.isNull()) {
            return JsonNull.INSTANCE;
        }
        @SuppressWarnings("rawtypes") JsonTypeAdapter innerAdapter = registry.getTypeAdapter(innerType);
        try {
            //noinspection unchecked
            return innerAdapter.toJson(value.getValueOrNull(), innerType);
        } catch (JsonGenerationException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonGenerationException(e);
        }
    }

}
