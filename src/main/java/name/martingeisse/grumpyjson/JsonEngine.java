/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyjson.builtin.ImmutableListAdapter;
import name.martingeisse.grumpyjson.builtin.IntegerAdapter;
import name.martingeisse.grumpyjson.builtin.json.JsonElementAdapter;
import name.martingeisse.grumpyjson.builtin.StringAdapter;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

public class JsonEngine {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final JsonRegistry registry = new JsonRegistry();

    public JsonEngine() {
        addTypeAdapter(new JsonElementAdapter());
        addTypeAdapter(new IntegerAdapter());
        addTypeAdapter(new StringAdapter());
        addTypeAdapter(new ImmutableListAdapter(registry));
    }

    public <T> void addTypeAdapter(JsonTypeAdapter<T> adapter) {
        Objects.requireNonNull(adapter, "adapter");
        registry.addTypeAdapter(adapter);
    }

    public JsonRegistry getRegistry() {
        return registry;
    }

    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        return registry.supportsType(type);
    }

    public boolean supportsType(TypeToken<?> typeToken) {
        Objects.requireNonNull(typeToken, "type");
        return registry.supportsType(typeToken.getType());
    }

    // -----------------------------------------------------------------------
    // parse
    // -----------------------------------------------------------------------

    public <T> T parse(String source, Class<T> clazz) throws JsonValidationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");
        return parse(wrapSource(source), clazz);
    }

    public <T> T parse(String source, TypeToken<T> typeToken) throws JsonValidationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(typeToken, "typeToken");
        return parse(wrapSource(source), typeToken);
    }

    public Object parse(String source, Type type) throws JsonValidationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        return parse(wrapSource(source), type);
    }

    public <T> T parse(InputStream source, Class<T> clazz) throws JsonValidationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");
        return parse(wrapSource(source), clazz);
    }

    public <T> T parse(InputStream source, TypeToken<T> typeToken) throws JsonValidationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(typeToken, "typeToken");
        return parse(wrapSource(source), typeToken);
    }

    public Object parse(InputStream source, Type type) throws JsonValidationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        return parse(wrapSource(source), type);
    }

    public <T> T parse(Reader source, Class<T> clazz) throws JsonValidationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");
        return clazz.cast(parse(source, (Type) clazz));
    }

    public <T> T parse(Reader source, TypeToken<T> typeToken) throws JsonValidationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(typeToken, "typeToken");
        //noinspection unchecked
        return (T)parse(source, typeToken.getType());
    }

    public Object parse(Reader source, Type type) throws JsonValidationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        return registry.getTypeAdapter(type).fromJson(gson.fromJson(source, JsonElement.class), type);
    }

    // -----------------------------------------------------------------------
    // stringify / writeTo
    // -----------------------------------------------------------------------

    public String stringify(Object value) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        return stringDestination(writer -> writeTo(value, writer));
    }

    public String stringify(Object value, TypeToken<?> typeToken) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(typeToken, "typeToken");
        return stringDestination(writer -> writeTo(value, typeToken, writer));
    }

    public String stringify(Object value, Type type) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        return stringDestination(writer -> writeTo(value, type, writer));
    }

    public void writeTo(Object value, OutputStream destination) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(destination, "destination");
        wrapDestination(destination, writer -> writeTo(value, writer));
    }

    public void writeTo(Object value, TypeToken<?> typeToken, OutputStream destination) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(typeToken, "typeToken");
        Objects.requireNonNull(destination, "destination");
        wrapDestination(destination, writer -> writeTo(value, typeToken, writer));
    }

    public void writeTo(Object value, Type type, OutputStream destination) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(destination, "destination");
        wrapDestination(destination, writer -> writeTo(value, type, writer));
    }

    public void writeTo(Object value, Writer destination) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(destination, "destination");
        writeTo(value, value.getClass(), destination);
    }

    public void writeTo(Object value, TypeToken<?> typeToken, Writer destination) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(typeToken, "typeToken");
        Objects.requireNonNull(destination, "destination");
        writeTo(value, typeToken.getType(), destination);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void writeTo(Object value, Type type, Writer destination) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(destination, "destination");
        JsonTypeAdapter adapter = registry.getTypeAdapter(type);
        gson.toJson(adapter.toJson(value, type), destination);
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
