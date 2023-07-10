/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.*;
import name.martingeisse.grumpyjson.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * The {@link JsonTypeAdapter} for {@link OptionalField}.
 * <p>
 * This adapter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistry#clearTypeAdapters()}.
 */
public class OptionalFieldConverter implements JsonTypeAdapter<OptionalField<?>> {

    private final JsonRegistry registry;

    /**
     * Constructor.
     *
     * @param registry the JSON registry -- needed to fetch the adapter for the contained type at run-time
     */
    public OptionalFieldConverter(JsonRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean supportsType(Type type) {
        return TypeUtil.isSingleParameterizedType(type, OptionalField.class) != null;
    }

    @Override
    public OptionalField<?> deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Type innerType = getInner(type);
        JsonTypeAdapter<?> innerAdapter = registry.getTypeAdapter(innerType);
        try {
            return OptionalField.ofValue(innerAdapter.deserialize(json, innerType));
        } catch (JsonDeserializationException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonDeserializationException(e);
        }
    }

    @Override
    public OptionalField<?> deserializeAbsent(Type type) {
        getInner(type);
        return OptionalField.ofNothing();
    }

    @Override
    public JsonElement serialize(OptionalField<?> value, Type type) throws JsonSerializationException {
        throw new JsonSerializationException("found OptionalField in a non-vanishable context");
    }

    @Override
    public Optional<JsonElement> serializeOptional(OptionalField<?> value, Type type) throws JsonSerializationException {
        Type innerType = getInner(type);
        if (value.isAbsent()) {
            return Optional.empty();
        }
        @SuppressWarnings("rawtypes") JsonTypeAdapter innerAdapter = registry.getTypeAdapter(innerType);
        try {
            //noinspection unchecked
            return Optional.of(innerAdapter.serialize(value.getValueOrNothingAsNull(), innerType));
        } catch (JsonSerializationException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonSerializationException(e);
        }

    }

    private Type getInner(Type outer) {
        return TypeUtil.expectSingleParameterizedType(outer, OptionalField.class);
    }

}
