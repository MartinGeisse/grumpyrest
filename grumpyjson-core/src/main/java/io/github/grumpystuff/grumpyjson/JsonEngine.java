/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson;


import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializationException;
import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializer;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializationException;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

/**
 * This class is the main entry point into the JSON conversion system.
 * <p>
 * This class should be used in two phases, configuration phase and runtime phase. The transition between the two
 * phases is called sealing the engine. Simply put, first call configuration methods only, then seal the engine, then
 * call runtime methods only. The behavior of this class is undefined if this sequence is not adhered to, and this
 * class is expected to throw an exception in such a case.
 * <p>
 * A new instance of this class has standard converters for convenience. More converters can be added using
 * {@link #registerDualConverter(JsonSerializer)}, {@link #registerSerializer(JsonSerializer)} and
 * {@link #registerDeserializer(JsonDeserializer)}. If the standard converters are not desired, you can call
 * {@link #getRegistries()} / {@link #getSerializerRegistry()} / {@link #getDeserializerRegistry()} and then .clear()
 * to remove all currently registered converters. Note that you can override standard converters just by adding your
 * own ones, since later-added converters will take precedence.
 * <p>
 * This class is abstract because it delegates handling the actual JSON syntax to a JSON library such as Gson or
 * Jackson. Concrete implementations implement the glue code for the various JSON libraries.
 */
public abstract class JsonEngine extends StructuralJsonEngine {

    /**
     * Creates a new JSON engine with standard converters registered.
     */
    public JsonEngine() {
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
    public abstract <T> T deserialize(String source, Class<T> clazz) throws JsonDeserializationException;

    /**
     * deserializes JSON from a {@link String}.
     *
     * @param source the source string
     * @param typeToken a type token for the target type to deserialize to
     * @return the deserialized value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public abstract <T> T deserialize(String source, TypeToken<T> typeToken) throws JsonDeserializationException;

    /**
     * deserializes JSON from a {@link String}.
     *
     * @param source the source string
     * @param type the target type to deserialize to
     * @return the deserialized value
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public abstract Object deserialize(String source, Type type) throws JsonDeserializationException;

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
    public abstract <T> T deserialize(InputStream source, Class<T> clazz) throws JsonDeserializationException;

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
    public abstract <T> T deserialize(InputStream source, TypeToken<T> typeToken) throws JsonDeserializationException;

    /**
     * deserializes JSON from an {@link InputStream}. As demanded by the MIME type application/json, the input must be
     * UTF-8 encoded.
     *
     * @param source the source stream
     * @param type the target type to deserialize to
     * @return the deserialized value
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public abstract Object deserialize(InputStream source, Type type) throws JsonDeserializationException;

    /**
     * deserializes JSON from an {@link Reader}.
     *
     * @param source the source reader
     * @param clazz the target type to deserialize to
     * @return the deserialized value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public abstract <T> T deserialize(Reader source, Class<T> clazz) throws JsonDeserializationException;

    /**
     * deserializes JSON from an {@link Reader}.
     *
     * @param source the source reader
     * @param typeToken a type token for the target type to deserialize to
     * @return the deserialized value
     * @param <T> the static target type
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public abstract <T> T deserialize(Reader source, TypeToken<T> typeToken) throws JsonDeserializationException;

    /**
     * deserializes JSON from an {@link Reader}.
     *
     * @param source the source reader
     * @param type the target type to deserialize to
     * @return the deserialized value
     * @throws JsonDeserializationException if the JSON is malformed or does not match the target type
     */
    public abstract Object deserialize(Reader source, Type type) throws JsonDeserializationException;

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
    public abstract String serializeToString(Object value) throws JsonSerializationException;

    /**
     * Turns a value into JSON that is written to an output stream. As demanded by the MIME type application/json,
     * the output will be UTF-8 encoded.
     *
     * @param value the value to convert
     * @param destination the stream to write to
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    public abstract void writeTo(Object value, OutputStream destination) throws JsonSerializationException;

    /**
     * Turns a value into JSON that is written to a writer.
     *
     * @param value the value to convert
     * @param destination the writer to write to
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned into JSON
     */
    public abstract void writeTo(Object value, Writer destination) throws JsonSerializationException;

}
