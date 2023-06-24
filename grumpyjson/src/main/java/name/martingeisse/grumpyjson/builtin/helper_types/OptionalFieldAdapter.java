/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.*;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * The {@link JsonTypeAdapter} for {@link OptionalField}.
 * <p>
 * This adapter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistry#clearTypeAdapters()}.
 */
public class OptionalFieldAdapter implements JsonTypeAdapter<OptionalField<?>> {

    private final JsonRegistry registry;

    /**
     * Constructor.
     *
     * @param registry the JSON registry -- needed to fetch the adapter for the contained type at run-time
     */
    public OptionalFieldAdapter(JsonRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean supportsType(Type type) {
        return TypeUtil.isSingleParameterizedType(type, OptionalField.class) != null;
    }

    @Override
    public OptionalField<?> fromJson(JsonElement json, Type type) throws JsonValidationException {
        Type innerType = getInner(type);
        JsonTypeAdapter<?> innerAdapter = registry.getTypeAdapter(innerType);
        try {
            return OptionalField.ofValue(innerAdapter.fromJson(json, innerType));
        } catch (JsonValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonValidationException(e);
        }
    }

    @Override
    public OptionalField<?> fromAbsentJson(Type type) {
        getInner(type);
        return OptionalField.ofNothing();
    }

    @Override
    public JsonElement toJson(OptionalField<?> value, Type type) throws JsonGenerationException {
        throw new JsonGenerationException("found OptionalField in a non-vanishable context");
    }

    @Override
    public Optional<JsonElement> toOptionalJson(OptionalField<?> value, Type type) throws JsonGenerationException {
        Type innerType = getInner(type);
        if (value.isAbsent()) {
            return Optional.empty();
        }
        @SuppressWarnings("rawtypes") JsonTypeAdapter innerAdapter = registry.getTypeAdapter(innerType);
        try {
            //noinspection unchecked
            return Optional.of(innerAdapter.toJson(value.getValueOrNothingAsNull(), innerType));
        } catch (JsonGenerationException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonGenerationException(e);
        }

    }

    private Type getInner(Type outer) {
        return TypeUtil.expectSingleParameterizedType(outer, OptionalField.class);
    }

}
