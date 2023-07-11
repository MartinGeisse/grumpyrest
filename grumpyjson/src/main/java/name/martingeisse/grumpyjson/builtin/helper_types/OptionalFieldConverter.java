/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.JsonRegistries;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;
import name.martingeisse.grumpyjson.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * The converter for {@link OptionalField}.
 * <p>
 * This converter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clear()}.
 */
public class OptionalFieldConverter implements JsonSerializer<OptionalField<?>>, JsonDeserializer {

    private final JsonRegistries registries;

    /**
     * Constructor.
     *
     * @param registries needed to fetch the converter for the contained type at run-time
     */
    public OptionalFieldConverter(JsonRegistries registries) {
        this.registries = registries;
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        return TypeUtil.isSingleParameterizedType(type, OptionalField.class) != null;
    }

    @Override
    public OptionalField<?> deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        try {
            return OptionalField.ofValue(registries.deserialize(json, getInner(type)));
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
    public boolean supportsClassForSerialization(Class<?> clazz) {
        return clazz.equals(OptionalField.class);
    }

    @Override
    public JsonElement serialize(OptionalField<?> value) throws JsonSerializationException {
        throw new JsonSerializationException("found OptionalField in a non-vanishable context");
    }

    @Override
    public Optional<JsonElement> serializeOptional(OptionalField<?> value) throws JsonSerializationException {
        if (value.isAbsent()) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(registries.serialize(value.getValueOrNothingAsNull()));
            } catch (Exception e) {
                throw new JsonSerializationException(e);
            }
        }
    }

    private Type getInner(Type outer) {
        return TypeUtil.expectSingleParameterizedType(outer, OptionalField.class);
    }

}
