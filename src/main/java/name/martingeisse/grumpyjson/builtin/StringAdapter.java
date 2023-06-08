package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyjson.JsonTypeAdapter;
import name.martingeisse.grumpyjson.JsonValidationException;

import java.util.Objects;

public class StringAdapter implements JsonTypeAdapter<String> {

    @Override
    public boolean supportsType(TypeToken<?> type) {
        return Objects.requireNonNull(type, "type").getType().equals(String.class);
    }

    @Override
    public String fromJson(JsonElement json, TypeToken<? super String> type) throws JsonValidationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        if (json instanceof JsonPrimitive primitive) {
            if (primitive.isString()) {
                return primitive.getAsString();
            }
        }
        throw new JsonValidationException("expected int, found: " + json);
    }

    @Override
    public JsonElement toJson(String value, TypeToken<? super String> type) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        return new JsonPrimitive(value);
    }

}
