/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import name.martingeisse.grumpyjson.JsonGenerationException;
import name.martingeisse.grumpyjson.JsonTypeAdapter;
import name.martingeisse.grumpyjson.JsonValidationException;

import java.lang.reflect.Type;
import java.util.Objects;

public class JsonMustBeNullAdapter implements JsonTypeAdapter<JsonMustBeNull> {

    @Override
    public boolean supportsType(Type type) {
        return type.equals(JsonMustBeNull.class);
    }

    @Override
    public JsonMustBeNull fromJson(JsonElement json, Type type) throws JsonValidationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        if (json instanceof JsonNull) {
            return JsonMustBeNull.INSTANCE;
        }
        throw new JsonValidationException("expected null, found: " + json);
    }

    @Override
    public JsonElement toJson(JsonMustBeNull value, Type type) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        return JsonNull.INSTANCE;
    }

}
