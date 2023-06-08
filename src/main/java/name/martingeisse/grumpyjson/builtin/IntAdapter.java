package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyjson.JsonGenerationException;
import name.martingeisse.grumpyjson.JsonTypeAdapter;
import name.martingeisse.grumpyjson.JsonValidationException;

import java.util.Objects;

public class IntAdapter implements JsonTypeAdapter<Integer> {

    @Override
    public Integer fromJson(JsonElement json, TypeToken<? super Integer> type) throws JsonValidationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        if (json instanceof JsonPrimitive primitive) {
            if (primitive.isNumber()) {
                long longValue = primitive.getAsLong();
                // test for float
                if (primitive.equals(new JsonPrimitive(longValue))) {
                    // test for too-large-for-int
                    int intValue = (int)longValue;
                    if (longValue != (long)intValue) {
                        throw new JsonValidationException("value out of bounds: " + longValue);
                    }
                    return intValue;
                }
            }
        }

        throw new JsonValidationException("expected int, found: " + json);
    }

    @Override
    public JsonElement toJson(Integer value, TypeToken<? super Integer> type) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        return new JsonPrimitive(value);
    }

}
