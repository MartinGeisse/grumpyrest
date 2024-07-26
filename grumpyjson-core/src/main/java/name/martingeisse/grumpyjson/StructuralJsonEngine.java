/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;


import name.martingeisse.grumpyjson.builtin.*;
import name.martingeisse.grumpyjson.builtin.helper_types.FieldMustBeNullConverter;
import name.martingeisse.grumpyjson.builtin.helper_types.NullableFieldConverter;
import name.martingeisse.grumpyjson.builtin.helper_types.OptionalFieldConverter;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializerRegistry;
import name.martingeisse.grumpyjson.json_model.JsonElement;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;
import name.martingeisse.grumpyjson.serialize.JsonSerializerRegistry;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * This base class of {@link JsonEngine} implements the "conversion" part of the JSON engine and can be used if
 * actual (de-)serialization from and to JSON syntax is not needed. In turn, it does not have any dependency on any
 * concrete JSON syntax implementation such as Gson or Jackson, but operates purely on the structural JSON model
 * defined in the json_model subpackage.
 * <p>
 * Please read the JsonEngine documentation for usage instructions.
 */
public class StructuralJsonEngine {

    private final JsonRegistries registries;

    /**
     * Creates a new JSON engine with standard converters registered.
     */
    public StructuralJsonEngine() {
        registries = JsonRegistries.createDefault();

        // Java types
        registerDualConverter(new BooleanConverter());
        registerDualConverter(new IntegerConverter());
        registerDualConverter(new LongConverter());
        registerDualConverter(new StringConverter());

        // collection types
        registerDualConverter(new ListConverter(registries));
        registerDualConverter(new MapConverter(registries));

        // helper types
        registerDualConverter(new FieldMustBeNullConverter());
        registerDualConverter(new NullableFieldConverter(registries));
        registerDualConverter(new OptionalFieldConverter(registries));
        registerDualConverter(new JsonElementConverter());

    }

    /**
     * Registers the specified serializer.
     *
     * @param serializer the serializer to register
     */
    public final void registerSerializer(JsonSerializer<?> serializer) {
        registries.registerSerializer(serializer);
    }

    /**
     * Registers the specified deserializer.
     *
     * @param deserializer the deserializer to register
     */
    public final void registerDeserializer(JsonDeserializer deserializer) {
        registries.registerDeserializer(deserializer);
    }

    /**
     * Registers the specified dual converter.
     *
     * @param converter the dual converter to register
     * @param <T>       the dual converter type which must implement both {@link JsonSerializer} and
     * {@link JsonDeserializer}
     */
    public final <T extends JsonSerializer<?> & JsonDeserializer> void registerDualConverter(T converter) {
        registries.registerDualConverter(converter);
    }

    /**
     * Getter method for the registries for converters.
     *
     * @return the registries
     */
    public final JsonRegistries getRegistries() {
        return registries;
    }

    /**
     * Getter method for the registry for serializers.
     *
     * @return the registry for serializers
     */
    public final JsonSerializerRegistry getSerializerRegistry() {
        return registries.serializerRegistry();
    }

    /**
     * Getter method for the registry for deserializers.
     *
     * @return the registry for deserializers
     */
    public final JsonDeserializerRegistry getDeserializerRegistry() {
        return registries.deserializerRegistry();
    }

    /**
     * Seals this JSON engine, moving from the configuration phase to the run-time phase.
     */
    public void seal() {
        registries.seal();
    }

    /**
     * Checks whether the specified class is supported for serialization by this engine
     *
     * @param clazz the class to check
     * @return true if supported, false if not
     */
    public final boolean supportsClassForSerialization(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");

        return registries.supportsClassForSerialization(clazz);
    }

    /**
     * Checks whether the specified type is supported for deserialization by this engine
     *
     * @param type the type to check
     * @return true if supported, false if not
     */
    public final boolean supportsTypeForDeserialization(Type type) {
        Objects.requireNonNull(type, "type");

        return registries.supportsTypeForDeserialization(type);
    }

    // -----------------------------------------------------------------------
    // deserialize
    // -----------------------------------------------------------------------

    /**
     * deserializes JSON from a {@link JsonElement}.
     *
     * @param source the source element
     * @param clazz the target type to deserialize to
     * @return the deserialized value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON does not match the target type
     */
    public final <T> T deserialize(JsonElement source, Class<T> clazz) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");

        return clazz.cast(deserialize(source, (Type) clazz));
    }

    /**
     * deserializes JSON from a {@link JsonElement}.
     *
     * @param source the source element
     * @param typeToken a type token for the target type to deserialize to
     * @return the deserialized value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON does not match the target type
     */
    public final <T> T deserialize(JsonElement source, TypeToken<T> typeToken) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(typeToken, "typeToken");

        //noinspection unchecked
        return (T) deserialize(source, typeToken.getType());
    }

    /**
     * deserializes JSON from a {@link JsonElement}.
     *
     * @param source the source element
     * @param type the target type to deserialize to
     * @return the deserialized value
     * @throws JsonDeserializationException if the JSON does not match the target type
     */
    public final Object deserialize(JsonElement source, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");

        return registries.deserialize(source, type);
    }

    // -----------------------------------------------------------------------
    // serialize
    // -----------------------------------------------------------------------

    /**
     * Turns a value into a {@link JsonElement}.
     *
     * @param value the value to convert
     * @return the JSON element
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    public final JsonElement toJsonElement(Object value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");

        return registries.serialize(value);
    }

}
