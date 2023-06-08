package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyjson.JsonGenerationException;
import name.martingeisse.grumpyjson.JsonTypeAdapter;
import name.martingeisse.grumpyjson.JsonValidationException;

public class StringAdapter implements JsonTypeAdapter<String> {

    @Override
    public String fromJson(JsonElement json, TypeToken<? super String> type) throws JsonValidationException {
        if (json instanceof JsonPrimitive primitive) {
            if (primitive.isString()) {
                return primitive.getAsString();
            }
        }
        throw new JsonValidationException("expected int, found: " + json);
    }

    @Override
    public JsonElement toJson(String value, TypeToken<? super String> type) {
        if (value == null) {
            throw JsonGenerationException.fieldIsNull();
        }
        return new JsonPrimitive(value);
    }

}
