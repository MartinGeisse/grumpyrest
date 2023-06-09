/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import name.martingeisse.grumpyjson.JsonGenerationException;
import name.martingeisse.grumpyjson.JsonRegistry;
import name.martingeisse.grumpyjson.JsonTypeAdapter;
import name.martingeisse.grumpyjson.JsonValidationException;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * The {@link JsonTypeAdapter} for {@link FieldMustBeNull}.
 * <p>
 * This adapter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistry#clearTypeAdapters()}.
 */
public class FieldMustBeNullAdapter implements JsonTypeAdapter<FieldMustBeNull> {

    /**
     * Constructor
     */
    public FieldMustBeNullAdapter() {
        // needed to silence Javadoc error because the implicit constructor doesn't have a doc comment
    }

    @Override
    public boolean supportsType(Type type) {
        return type.equals(FieldMustBeNull.class);
    }

    @Override
    public FieldMustBeNull fromJson(JsonElement json, Type type) throws JsonValidationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        if (json instanceof JsonNull) {
            return FieldMustBeNull.INSTANCE;
        }
        throw new JsonValidationException("expected null, found: " + json);
    }

    @Override
    public JsonElement toJson(FieldMustBeNull value, Type type) throws JsonGenerationException {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        return JsonNull.INSTANCE;
    }

}
