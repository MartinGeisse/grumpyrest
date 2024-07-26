/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.json_model.JsonElement;
import name.martingeisse.grumpyjson.json_model.JsonString;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A converter for enum types.
 * <p>
 * Converters of this type will be auto-generated for unknown enum types.
 *
 * @param <T> the enum type to convert
 */
public final class EnumConverter<T extends Enum<T>> implements JsonSerializer<T>, JsonDeserializer {

    private final Class<T> enumClass;

    /**
     * Constructor
     *
     * @param enumClass the enum class to parse
     */
    public EnumConverter(Class<T> enumClass) {
        Objects.requireNonNull(enumClass, "enumClass");

        this.enumClass = enumClass;
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        Objects.requireNonNull(type, "type");

        return type.equals(enumClass);
    }

    @Override
    public Object deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        String text = json.deserializerExpectsString();
        try {
            return Enum.valueOf(enumClass, text);
        } catch (IllegalArgumentException e) {
            throw new JsonDeserializationException("unknown value");
        }
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");

        return clazz.equals(enumClass);
    }

    @Override
    public JsonElement serialize(T value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");

        return JsonString.of(value.name());
    }

}
