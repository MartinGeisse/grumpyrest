/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyjson.builtin.*;
import name.martingeisse.grumpyjson.builtin.helper_types.FieldMustBeNullConverter;
import name.martingeisse.grumpyjson.builtin.helper_types.NullableFieldConverter;
import name.martingeisse.grumpyjson.builtin.helper_types.OptionalFieldConverter;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializerRegistry;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;
import name.martingeisse.grumpyjson.serialize.JsonSerializerRegistry;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the main entry point into the JSON conversion system.
 * <p>
 * This class should be used in two phases, configuration phase and runtime phase. The transition between the two
 * phases is called sealing the engine. Simply put, first call configuration methods only, then seal the engine, then
 * call runtime methods only. The behavior of this class is undefined if this sequence is not adhered to, and this
 * class is expected to throw an exception in such a case.
 * <p>
 * A new instance of this class has standard converters for convenience. More converters can be
 * added using {@link #registerDualConverter(name.martingeisse.grumpyjson.serialize.JsonSerializer)},
 * {@link #registerSerializer(name.martingeisse.grumpyjson.serialize.JsonSerializer)} and
 * {@link #registerDeserializer(name.martingeisse.grumpyjson.deserialize.JsonDeserializer)}. If the standard
 * converters are not desired, you can call {@link #getRegistries()} / {@link #getSerializerRegistry()} /
 * {@link #getDeserializerRegistry()} and then .clear() to remove all currently registered converters. Note that you
 * can override standard converters just by adding your own ones, since later-added converters will take precedence.
 */
public final class JsonEngine {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private final JsonRegistries registries;

    /**
     * Creates a new JSON engine with standard converters registered.
     */
    public JsonEngine() {
        registries = JsonRegistries.createDefault();

        // Java types
        registerDualConverter(new BooleanConverter());
        registerDualConverter(new IntegerConverter());
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
    public void registerSerializer(name.martingeisse.grumpyjson.serialize.JsonSerializer<?> serializer) {
        registries.registerSerializer(serializer);
    }

    /**
     * Registers the specified deserializer.
     *
     * @param deserializer the deserializer to register
     */
    public void registerDeserializer(name.martingeisse.grumpyjson.deserialize.JsonDeserializer deserializer) {
        registries.registerDeserializer(deserializer);
    }

    /**
     * Registers the specified dual converter.
     *
     * @param converter the dual converter to register
     */
    public <T extends JsonSerializer<?> & JsonDeserializer> void registerDualConverter(T converter) {
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
    public boolean supportsClassForSerialization(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");
        return registries.supportsClassForSerialization(clazz);
    }

    /**
     * Checks whether the specified type is supported for deserialization by this engine
     *
     * @param type the type to check
     * @return true if supported, false if not
     */
    public boolean supportsTypeForDeserialization(Type type) {
        Objects.requireNonNull(type, "type");
        return registries.supportsTypeForDeserialization(type);
    }

    // -----------------------------------------------------------------------
    // deserialize
    // -----------------------------------------------------------------------

    /**
     * deserializes JSON from a {@link String}.
     *
     * @param source the source string
     * @param clazz the target type to deserialize to
     * @return the deserialized value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public <T> T deserialize(String source, Class<T> clazz) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");
        return deserialize(wrapSource(source), clazz);
    }

    /**
     * deserializes JSON from a {@link String}.
     *
     * @param source the source string
     * @param typeToken a type token for the target type to deserialize to
     * @return the deserialized value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public <T> T deserialize(String source, TypeToken<T> typeToken) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(typeToken, "typeToken");
        return deserialize(wrapSource(source), typeToken);
    }

    /**
     * deserializes JSON from a {@link String}.
     *
     * @param source the source string
     * @param type the target type to deserialize to
     * @return the deserialized value
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public Object deserialize(String source, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        return deserialize(wrapSource(source), type);
    }

    /**
     * deserializes JSON from an {@link InputStream}. As demanded by the MIME type application/json, the input must be
     * UTF-8 encoded.
     *
     * @param source the source stream
     * @param clazz the target type to deserialize to
     * @return the deserialized value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public <T> T deserialize(InputStream source, Class<T> clazz) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");
        return deserialize(wrapSource(source), clazz);
    }

    /**
     * deserializes JSON from an {@link InputStream}. As demanded by the MIME type application/json, the input must be
     * UTF-8 encoded.
     *
     * @param source the source stream
     * @param typeToken a type token for the target type to deserialize to
     * @return the deserialized value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public <T> T deserialize(InputStream source, TypeToken<T> typeToken) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(typeToken, "typeToken");
        return deserialize(wrapSource(source), typeToken);
    }

    /**
     * deserializes JSON from an {@link InputStream}. As demanded by the MIME type application/json, the input must be
     * UTF-8 encoded.
     *
     * @param source the source stream
     * @param type the target type to deserialize to
     * @return the deserialized value
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public Object deserialize(InputStream source, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        return deserialize(wrapSource(source), type);
    }

    /**
     * deserializes JSON from an {@link Reader}.
     *
     * @param source the source reader
     * @param clazz the target type to deserialize to
     * @return the deserialized value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public <T> T deserialize(Reader source, Class<T> clazz) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");
        return clazz.cast(deserialize(source, (Type) clazz));
    }

    /**
     * deserializes JSON from an {@link Reader}.
     *
     * @param source the source reader
     * @param typeToken a type token for the target type to deserialize to
     * @return the deserialized value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public <T> T deserialize(Reader source, TypeToken<T> typeToken) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(typeToken, "typeToken");
        //noinspection unchecked
        return (T) deserialize(source, typeToken.getType());
    }

    /**
     * deserializes JSON from an {@link Reader}.
     *
     * @param source the source reader
     * @param type the target type to deserialize to
     * @return the deserialized value
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public Object deserialize(Reader source, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        JsonElement json;
        try {
            json = gson.fromJson(source, JsonElement.class);
        } catch (JsonSyntaxException e) {
            throw new JsonDeserializationException(mapGsonErrorMessage(e.getMessage()));
        } catch (JsonIOException e) {
            throw new JsonDeserializationException("I/O error while reading JSON");
        }
        if (json == null) {
            // this happens if the source does not even contain malformed JSON, but just nothing (EOF)
            throw new JsonDeserializationException("no JSON to deserialize");
        }
        return registries.deserialize(json, type);
    }

    // the message looks like this: "at line 1 column 20 path"
    private static final Pattern GSON_SYNTAX_ERROR_LOCATION_PATTERN = Pattern.compile("at line (\\d+) column (\\d+) ");

    /**
     * This method transforms the error message so it does not reveal too much internals.
     */
    private static String mapGsonErrorMessage(String message) {
        Matcher matcher = GSON_SYNTAX_ERROR_LOCATION_PATTERN.matcher(message);
        if (matcher.find()) {
            return "syntax error in JSON at line " + matcher.group(1) + ", column " + matcher.group(2);
        }
        return "syntax error in JSON";
    }

    /**
     * deserializes JSON from a {@link JsonElement}.
     *
     * @param source the source element
     * @param clazz the target type to deserialize to
     * @return the deserialized value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON does not match the target type
     */
    public <T> T deserialize(JsonElement source, Class<T> clazz) throws JsonDeserializationException {
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
    public <T> T deserialize(JsonElement source, TypeToken<T> typeToken) throws JsonDeserializationException {
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
    public Object deserialize(JsonElement source, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        return registries.deserialize(source, type);
    }

    // -----------------------------------------------------------------------
    // stringify / writeTo
    // -----------------------------------------------------------------------

    /**
     * Turns a value into a JSON string.
     *
     * @param value the value to serialize
     * @return the JSON string
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    public String serializeToString(Object value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        StringWriter writer = new StringWriter();
        writeTo(value, writer);
        return writer.toString();
    }

    /**
     * Turns a value into JSON that is written to an output stream. As demanded by the MIME type application/json,
     * the output will be UTF-8 encoded.
     *
     * @param value the value to convert
     * @param destination the stream to write to
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    public void writeTo(Object value, OutputStream destination) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(destination, "destination");
        OutputStreamWriter writer = new OutputStreamWriter(destination, StandardCharsets.UTF_8);
        writeTo(value, writer);
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Ignore. This can happen if the network connection closes unexpectedly. There is no use in logging this,
            // and we cannot tell the client about it either.
        }
    }

    /**
     * Turns a value into JSON that is written to a writer.
     *
     * @param value the value to convert
     * @param destination the writer to write to
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    public void writeTo(Object value, Writer destination) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(destination, "destination");
        gson.toJson(registries.serialize(value), destination);
    }

    /**
     * Turns a value into a {@link JsonElement}.
     *
     * @param value the value to convert
     * @return the JSON element
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    public JsonElement toJsonElement(Object value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        return registries.serialize(value);
    }

    // -----------------------------------------------------------------------
    // helpers
    // -----------------------------------------------------------------------

    private static Reader wrapSource(String source) {
        return new StringReader(source);
    }

    private static Reader wrapSource(InputStream source) {
        return new InputStreamReader(source, StandardCharsets.UTF_8);
    }

}
