package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyjson.JsonTypeAdapter;

import java.util.Objects;

public final class JsonElementAdapter implements JsonTypeAdapter<JsonElement> {

    @Override
    public boolean supportsType(TypeToken<?> type) {
        Objects.requireNonNull(type, "type");
        return (type.getType() instanceof Class<?> clazz) && JsonElement.class.isAssignableFrom(clazz);
    }

    @Override
    public JsonElement fromJson(JsonElement json, TypeToken<? super JsonElement> type) {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        return json.deepCopy();
    }

    @Override
    public JsonElement toJson(JsonElement value, TypeToken<? super JsonElement> type) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        return value.deepCopy();
    }

}
