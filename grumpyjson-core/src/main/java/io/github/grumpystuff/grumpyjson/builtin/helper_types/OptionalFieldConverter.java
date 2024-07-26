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
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializationException;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializer;
import io.github.grumpystuff.grumpyjson.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

/**
 * The converter for {@link OptionalField}.
 * <p>
 * This converter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clear()}.
 */
public final class OptionalFieldConverter implements JsonSerializer<OptionalField<?>>, JsonDeserializer {

    private final JsonProviders providers;

    /**
     * Constructor.
     *
     * @param providers needed to fetch the converter for the contained type at run-time
     */
    public OptionalFieldConverter(JsonProviders providers) {
        Objects.requireNonNull(providers, "providers");

        this.providers = providers;
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        Objects.requireNonNull(type, "type");

        return TypeUtil.isSingleParameterizedType(type, OptionalField.class) != null;
    }

    @Override
    public OptionalField<?> deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        try {
            return OptionalField.ofValue(providers.deserialize(json, getInner(type)));
        } catch (JsonDeserializationException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonDeserializationException(e);
        }
    }

    @Override
    public OptionalField<?> deserializeAbsent(Type type) {
        Objects.requireNonNull(type, "type");

        getInner(type);
        return OptionalField.ofNothing();
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");

        return clazz.equals(OptionalField.class);
    }

    @Override
    public JsonElement serialize(OptionalField<?> value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");

        throw new JsonSerializationException("found OptionalField in a non-vanishable context");
    }

    @Override
    public Optional<JsonElement> serializeOptional(OptionalField<?> value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");

        if (value.isAbsent()) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(providers.serialize(value.getValueOrNothingAsNull()));
            } catch (JsonSerializationException e) {
                throw e;
            } catch (Exception e) {
                throw new JsonSerializationException(e);
            }
        }
    }

    private Type getInner(Type outer) {
        Objects.requireNonNull(outer, "outer");

        return TypeUtil.expectSingleParameterizedType(outer, OptionalField.class);
    }

}
