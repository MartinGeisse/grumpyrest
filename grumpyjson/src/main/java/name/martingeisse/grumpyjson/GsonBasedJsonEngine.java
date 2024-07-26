/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;


import com.google.gson.*;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.json_model.JsonElement;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GSON-based implementation of {@link JsonEngine}.
 */
public final class GsonBasedJsonEngine extends JsonEngine {

    private final Gson gson = new GsonBuilder().setStrictness(Strictness.STRICT).setPrettyPrinting().serializeNulls().create();

    /**
     * Creates a new JSON engine with standard converters registered.
     */
    public GsonBasedJsonEngine() {
    }

    // -----------------------------------------------------------------------
    // deserialize
    // -----------------------------------------------------------------------

    @Override
    public <T> T deserialize(String source, Class<T> clazz) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");

        return deserialize(wrapSource(source), clazz);
    }

    @Override
    public <T> T deserialize(String source, TypeToken<T> typeToken) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(typeToken, "typeToken");

        return deserialize(wrapSource(source), typeToken);
    }

    @Override
    public Object deserialize(String source, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");

        return deserialize(wrapSource(source), type);
    }

    @Override
    public <T> T deserialize(InputStream source, Class<T> clazz) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");

        return deserialize(wrapSource(source), clazz);
    }

    @Override
    public <T> T deserialize(InputStream source, TypeToken<T> typeToken) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(typeToken, "typeToken");

        return deserialize(wrapSource(source), typeToken);
    }

    @Override
    public Object deserialize(InputStream source, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");

        return deserialize(wrapSource(source), type);
    }

    @Override
    public <T> T deserialize(Reader source, Class<T> clazz) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");

        return clazz.cast(deserialize(source, (Type) clazz));
    }

    @Override
    public <T> T deserialize(Reader source, TypeToken<T> typeToken) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(typeToken, "typeToken");

        //noinspection unchecked
        return (T) deserialize(source, typeToken.getType());
    }

    @Override
    public Object deserialize(Reader source, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");

        JsonElement json;
        try {
            com.google.gson.JsonElement gsonElement = gson.fromJson(source, com.google.gson.JsonElement.class);
            if (gsonElement == null) {
                throw new JsonDeserializationException("no JSON to deserialize");
            }
            json = GsonTreeMapper.mapFromGson(gsonElement);
        } catch (JsonSyntaxException e) {
            throw new JsonDeserializationException(mapGsonErrorMessage(e.getMessage()));
        } catch (JsonIOException e) {
            throw new JsonDeserializationException("I/O error while reading JSON");
        }
        if (json == null) {
            // this happens if the source does not even contain malformed JSON, but just nothing (EOF)
            throw new JsonDeserializationException("no JSON to deserialize");
        }
        return deserialize(json, type);
    }

    // the message looks like this: "at line 1 column 20 path"
    private static final Pattern GSON_SYNTAX_ERROR_LOCATION_PATTERN = Pattern.compile("at line (\\d+) column (\\d+) ");

    /**
     * This method transforms the error message so it does not reveal too much internals.
     */
    private static String mapGsonErrorMessage(String message) {
        Objects.requireNonNull(message, "message");

        Matcher matcher = GSON_SYNTAX_ERROR_LOCATION_PATTERN.matcher(message);
        if (matcher.find()) {
            return "syntax error in JSON at line " + matcher.group(1) + ", column " + matcher.group(2);
        }
        return "syntax error in JSON";
    }

    private static Reader wrapSource(String source) {
        Objects.requireNonNull(source, "source");

        return new StringReader(source);
    }

    private static Reader wrapSource(InputStream source) {
        Objects.requireNonNull(source, "source");

        return new InputStreamReader(source, StandardCharsets.UTF_8);
    }

    // -----------------------------------------------------------------------
    // stringify / writeTo
    // -----------------------------------------------------------------------

    @Override
    public String serializeToString(Object value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");

        StringWriter writer = new StringWriter();
        writeTo(value, writer);
        return writer.toString();
    }

    @Override
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

    @Override
    public void writeTo(Object value, Writer destination) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(destination, "destination");

        JsonElement json = toJsonElement(value);
        com.google.gson.JsonElement gsonElement = GsonTreeMapper.mapToGson(json);
        gson.toJson(gsonElement, destination);
    }

}
