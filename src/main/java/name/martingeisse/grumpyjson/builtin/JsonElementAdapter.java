package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.JsonTypeAdapter;

import java.lang.reflect.Type;
import java.util.Objects;

public final class JsonElementAdapter implements JsonTypeAdapter<JsonElement> {

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        return (type instanceof Class<?> clazz) && JsonElement.class.isAssignableFrom(clazz);
    }

    @Override
    public JsonElement fromJson(JsonElement json, Type type) {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        return json.deepCopy();
    }

    @Override
    public JsonElement toJson(JsonElement value, Type type) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        return value.deepCopy();
    }

}
