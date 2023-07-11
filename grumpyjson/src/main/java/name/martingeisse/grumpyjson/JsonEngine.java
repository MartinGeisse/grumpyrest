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
import name.martingeisse.grumpyjson.builtin.helper_types.TypeWrapperConverter;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the main entry point into the JSON conversion system.
 * <p>
 * This class should be used in two phases, configuration phase and runtime phase. Simply put, first call configuration
 * methods only, then call runtime methods only. The behavior of this class is undefined if a configuration method
 * gets called after a runtime method has been called (and this may be prevented in the future). The reason for this
 * is that some supporting data gets generated lazily at runtime, and will be out-of-date (but not detected as
 * out-of-date) if the configuration gets changed afterwards.
 * <p>
 * A new instance of this class has stanndard type adapters registered for convenience. More type adapters can be
 * added using {@link #registerTypeAdapter(JsonTypeAdapter)}. If the standard type adapters are not desired, you can call
 * {@link #getRegistry()} and then {@link JsonRegistries#clear()} to remove all currently registered type
 * adapters.
 */
public class JsonEngine {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private final JsonRegistries registry = new JsonRegistries();

    /**
     * Creates a new JSON engine with standard type adapters registered.
     */
    public JsonEngine() {

        // Java types
        registerTypeAdapter(new BooleanConverter());
        registerTypeAdapter(new IntegerConverter());
        registerTypeAdapter(new StringConverter());

        // collection types
        registerTypeAdapter(new ListConverter(registry));

        // helper types
        registerTypeAdapter(new FieldMustBeNullConverter());
        registerTypeAdapter(new NullableFieldConverter(registry));
        registerTypeAdapter(new OptionalFieldConverter(registry));
        registerTypeAdapter(new JsonElementConverter());
        registerTypeAdapter(new TypeWrapperConverter(registry));

    }

    /**
     * Adds the specified type adapter.
     *
     * @param adapter the type adapter to add
     */
    public void registerTypeAdapter(JsonTypeAdapter<?> adapter) {
        Objects.requireNonNull(adapter, "adapter");
        registry.register(adapter);
    }

    /**
     * Getter method for the registry for type adapters.
     *
     * @return the registry
     */
    public JsonRegistries getRegistry() {
        return registry;
    }

    /**
     * Checks whether the specified type is supported by this engine
     *
     * @param type the type to check
     * @return true if supporte, false if not
     */
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        return registry.supports(type);
    }

    /**
     * Checks whether the specified type is supported by this engine
     *
     * @param typeToken a type token for the type to check
     * @return true if supporte, false if not
     */
    public boolean supportsType(TypeToken<?> typeToken) {
        Objects.requireNonNull(typeToken, "type");
        return registry.supports(typeToken.getType());
    }

    // -----------------------------------------------------------------------
    // parse
    // -----------------------------------------------------------------------

    /**
     * Parses JSON from a {@link String}.
     *
     * @param source the source string
     * @param clazz the target type to parse to
     * @return the parsed value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public <T> T parse(String source, Class<T> clazz) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");
        return parse(wrapSource(source), clazz);
    }

    /**
     * Parses JSON from a {@link String}.
     *
     * @param source the source string
     * @param typeToken a type token for the target type to parse to
     * @return the parsed value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public <T> T parse(String source, TypeToken<T> typeToken) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(typeToken, "typeToken");
        return parse(wrapSource(source), typeToken);
    }

    /**
     * Parses JSON from a {@link String}.
     *
     * @param source the source string
     * @param type the target type to parse to
     * @return the parsed value
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public Object parse(String source, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        return parse(wrapSource(source), type);
    }

    /**
     * Parses JSON from an {@link InputStream}. As demanded by the MIME type application/json, the input must be
     * UTF-8 encoded.
     *
     * @param source the source stream
     * @param clazz the target type to parse to
     * @return the parsed value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public <T> T parse(InputStream source, Class<T> clazz) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");
        return parse(wrapSource(source), clazz);
    }

    /**
     * Parses JSON from an {@link InputStream}. As demanded by the MIME type application/json, the input must be
     * UTF-8 encoded.
     *
     * @param source the source stream
     * @param typeToken a type token for the target type to parse to
     * @return the parsed value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public <T> T parse(InputStream source, TypeToken<T> typeToken) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(typeToken, "typeToken");
        return parse(wrapSource(source), typeToken);
    }

    /**
     * Parses JSON from an {@link InputStream}. As demanded by the MIME type application/json, the input must be
     * UTF-8 encoded.
     *
     * @param source the source stream
     * @param type the target type to parse to
     * @return the parsed value
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public Object parse(InputStream source, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        return parse(wrapSource(source), type);
    }

    /**
     * Parses JSON from an {@link Reader}.
     *
     * @param source the source reader
     * @param clazz the target type to parse to
     * @return the parsed value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public <T> T parse(Reader source, Class<T> clazz) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");
        return clazz.cast(parse(source, (Type) clazz));
    }

    /**
     * Parses JSON from an {@link Reader}.
     *
     * @param source the source reader
     * @param typeToken a type token for the target type to parse to
     * @return the parsed value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public <T> T parse(Reader source, TypeToken<T> typeToken) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(typeToken, "typeToken");
        //noinspection unchecked
        return (T)parse(source, typeToken.getType());
    }

    /**
     * Parses JSON from an {@link Reader}.
     *
     * @param source the source reader
     * @param type the target type to parse to
     * @return the parsed value
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public Object parse(Reader source, Type type) throws JsonDeserializationException {
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
            throw new JsonDeserializationException("no JSON to parse");
        }
        return registry.get(type).deserialize(json, type);
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
     * Parses JSON from a {@link JsonElement}.
     *
     * @param source the source element
     * @param clazz the target type to parse to
     * @return the parsed value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON does not match the target type
     */
    public <T> T parse(JsonElement source, Class<T> clazz) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");
        return clazz.cast(parse(source, (Type) clazz));
    }

    /**
     * Parses JSON from a {@link JsonElement}.
     *
     * @param source the source element
     * @param typeToken a type token for the target type to parse to
     * @return the parsed value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON does not match the target type
     */
    public <T> T parse(JsonElement source, TypeToken<T> typeToken) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(typeToken, "typeToken");
        //noinspection unchecked
        return (T)parse(source, typeToken.getType());
    }

    /**
     * Parses JSON from a {@link JsonElement}.
     *
     * @param source the source element
     * @param type the target type to parse to
     * @return the parsed value
     * @throws JsonDeserializationException if the JSON does not match the target type
     */
    public Object parse(JsonElement source, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        return registry.get(type).deserialize(source, type);
    }

    // -----------------------------------------------------------------------
    // stringify / writeTo
    // -----------------------------------------------------------------------

    /**
     * Turns a value into a JSON string.
     *
     * @param value the value to convert
     * @return the JSON string
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    public String stringify(Object value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        return stringDestination(writer -> writeTo(value, writer));
    }

    /**
     * Turns a value into a JSON string.
     *
     * @param value the value to convert
     * @param typeToken a type token for type to convert. This is useful if the value is an instance of a generic
     *                  type and the static type arguments of that generic type are needed for conversion to JSON.
     * @return the JSON string
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    public String stringify(Object value, TypeToken<?> typeToken) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(typeToken, "typeToken");
        return stringDestination(writer -> writeTo(value, typeToken, writer));
    }

    /**
     * Turns a value into a JSON string.
     *
     * @param value the value to convert
     * @param type the type to convert. This is useful if the value is an instance of a generic
     *                  type and the static type arguments of that generic type are needed for conversion to JSON.
     * @return the JSON string
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    public String stringify(Object value, Type type) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        return stringDestination(writer -> writeTo(value, type, writer));
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
        wrapDestination(destination, writer -> writeTo(value, writer));
    }

    /**
     * Turns a value into JSON that is written to an output stream. As demanded by the MIME type application/json,
     * the output will be UTF-8 encoded.
     *
     * @param value the value to convert
     * @param typeToken a type token for the type to convert. This is useful if the value is an instance of a generic
     *                  type and the static type arguments of that generic type are needed for conversion to JSON.
     * @param destination the stream to write to
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    public void writeTo(Object value, TypeToken<?> typeToken, OutputStream destination) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(typeToken, "typeToken");
        Objects.requireNonNull(destination, "destination");
        wrapDestination(destination, writer -> writeTo(value, typeToken, writer));
    }

    /**
     * Turns a value into JSON that is written to an output stream. As demanded by the MIME type application/json,
     * the output will be UTF-8 encoded.
     *
     * @param value the value to convert
     * @param type the type to convert. This is useful if the value is an instance of a generic
     *                  type and the static type arguments of that generic type are needed for conversion to JSON.
     * @param destination the stream to write to
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    public void writeTo(Object value, Type type, OutputStream destination) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(destination, "destination");
        wrapDestination(destination, writer -> writeTo(value, type, writer));
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
        writeTo(value, value.getClass(), destination);
    }

    /**
     * Turns a value into JSON that is written to a writer.
     *
     * @param value the value to convert
     * @param typeToken a type token for the type to convert. This is useful if the value is an instance of a generic
     *                  type and the static type arguments of that generic type are needed for conversion to JSON.
     * @param destination the writer to write to
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    public void writeTo(Object value, TypeToken<?> typeToken, Writer destination) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(typeToken, "typeToken");
        Objects.requireNonNull(destination, "destination");
        writeTo(value, typeToken.getType(), destination);
    }

    /**
     * Turns a value into JSON that is written to a writer.
     *
     * @param value the value to convert
     * @param type the type to convert. This is useful if the value is an instance of a generic
     *                  type and the static type arguments of that generic type are needed for conversion to JSON.
     * @param destination the writer to write to
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void writeTo(Object value, Type type, Writer destination) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(destination, "destination");
        JsonTypeAdapter adapter = registry.get(type);
        gson.toJson(adapter.serialize(value, type), destination);
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
        return toJsonElement(value, value.getClass());
    }

    /**
     * Turns a value into a {@link JsonElement}.
     *
     * @param value the value to convert
     * @param typeToken a type token for the type to convert. This is useful if the value is an instance of a generic
     *                  type and the static type arguments of that generic type are needed for conversion to JSON.
     * @return the JSON element
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    public JsonElement toJsonElement(Object value, TypeToken<?> typeToken) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(typeToken, "typeToken");
        return toJsonElement(value, typeToken.getType());
    }

    /**
     * Turns a value into a {@link JsonElement}.
     *
     * @param value the value to convert
     * @param type the type to convert. This is useful if the value is an instance of a generic
     *                  type and the static type arguments of that generic type are needed for conversion to JSON.
     * @return the JSON element
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    public JsonElement toJsonElement(Object value, Type type) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        @SuppressWarnings("rawtypes") JsonTypeAdapter adapter = registry.get(type);
        //noinspection unchecked
        return adapter.serialize(value, type);
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

    private static String stringDestination(Consumer<Writer> consumer) {
        StringWriter writer = new StringWriter();
        consumer.accept(writer);
        return writer.toString();
    }

    private static void wrapDestination(OutputStream destination, Consumer<Writer> consumer) {
        OutputStreamWriter writer = new OutputStreamWriter(destination, StandardCharsets.UTF_8);
        consumer.accept(writer);
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Ignore. This can happen if the network connection closes unexpectedly. There is no use in logging this,
            // and we cannot tell the client about it either.
        }
    }

}
