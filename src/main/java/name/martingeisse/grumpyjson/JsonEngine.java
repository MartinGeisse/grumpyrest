package name.martingeisse.grumpyjson;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyjson.builtin.IntAdapter;
import name.martingeisse.grumpyjson.builtin.StringAdapter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class JsonEngine {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final JsonRegistry registry = new JsonRegistry();

    public JsonEngine() {
        registry.addTypeAdapter(TypeToken.get(Integer.TYPE), new IntAdapter());
        registry.addTypeAdapter(TypeToken.get(String.class), new StringAdapter());
    }

    public <T> void addTypeAdapter(TypeToken<T> type, JsonTypeAdapter<T> adapter) {
        registry.addTypeAdapter(type, adapter);
    }

    public JsonRegistry getRegistry() {
        return registry;
    }

    // -----------------------------------------------------------------------
    // parse
    // -----------------------------------------------------------------------

    public <T> T parse(String source, Class<T> clazz) throws JsonValidationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");
        return parse(source, TypeToken.get(clazz));
    }

    public <T> T parse(String source, TypeToken<T> type) throws JsonValidationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        return parse(new StringReader(source), type);
    }

    public <T> T parse(InputStream source, Class<T> clazz) throws JsonValidationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");
        return parse(source, TypeToken.get(clazz));
    }

    public <T> T parse(InputStream source, TypeToken<T> type) throws JsonValidationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        return parse(new InputStreamReader(source, StandardCharsets.UTF_8), type);
    }

    public <T> T parse(Reader source, Class<T> clazz) throws JsonValidationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(clazz, "clazz");
        return parse(source, TypeToken.get(clazz));
    }

    public <T> T parse(Reader source, TypeToken<T> type) throws JsonValidationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        return registry.getTypeAdapter(type).fromJson(gson.fromJson(source, JsonElement.class), type);
    }

    // -----------------------------------------------------------------------
    // stringify / writeTo
    // -----------------------------------------------------------------------

    public String stringify(Object value) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        return stringify(value, typeOf(value));
    }

    public String stringify(Object value, Class<?> clazz) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(clazz, "clazz");
        return stringify(value, TypeToken.get(clazz));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public String stringify(Object value, TypeToken<?> type) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        JsonTypeAdapter adapter = registry.getTypeAdapter(type);
        return gson.toJson(adapter.toJson(nonNull(value), type));
    }

    public void writeTo(Object value, Writer destination) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(destination, "destination");
        writeTo(value, typeOf(value), destination);
    }

    public void writeTo(Object value, Class<?> clazz, Writer destination) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(clazz, "clazz");
        Objects.requireNonNull(destination, "destination");
        writeTo(value, TypeToken.get(clazz), destination);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void writeTo(Object value, TypeToken<?> type, Writer destination) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(destination, "destination");
        JsonTypeAdapter adapter = registry.getTypeAdapter(type);
        gson.toJson(adapter.toJson(value, type), destination);
    }

    public void writeTo(Object value, OutputStream destination) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(destination, "destination");
        writeTo(value, new OutputStreamWriter(destination, StandardCharsets.UTF_8));
    }

    public void writeTo(Object value, Class<?> clazz, OutputStream destination) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(clazz, "clazz");
        Objects.requireNonNull(destination, "destination");
        writeTo(value, clazz, new OutputStreamWriter(destination, StandardCharsets.UTF_8));
    }

    public void writeTo(Object value, TypeToken<?> type, OutputStream destination) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(destination, "destination");
        writeTo(value, type, new OutputStreamWriter(destination, StandardCharsets.UTF_8));
    }

    // -----------------------------------------------------------------------
    // helpers
    // -----------------------------------------------------------------------

    private static TypeToken<?> typeOf(Object value) {
        return TypeToken.get(nonNull(value).getClass());
    }

    private static <T> T nonNull(T value) {
        if (value == null) {
            throw new IllegalArgumentException("cannot turn Java null to JSON");
        }
        return value;
    }

}
