package name.martingeisse.grumpyjson;

import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public interface JsonTypeAdapter<T> {
    boolean supportsType(Type type);
    T fromJson(JsonElement json, Type type) throws JsonValidationException;
    JsonElement toJson(T value, Type type) throws JsonGenerationException;
}
