/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.json;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.*;

import java.lang.reflect.Type;
import java.util.Optional;

public class JsonOptionalAdapter implements JsonTypeAdapter<JsonOptional<?>> {

    private final JsonRegistry registry;

    public JsonOptionalAdapter(JsonRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean supportsType(Type type) {
        return TypeUtil.isSingleParameterizedType(type, JsonOptional.class) != null;
    }

    @Override
    public JsonOptional<?> fromJson(JsonElement json, Type type) throws JsonValidationException {
        Type innerType = getInner(type);
        JsonTypeAdapter<?> innerAdapter = registry.getTypeAdapter(innerType);
        try {
            return JsonOptional.ofValue(innerAdapter.fromJson(json, innerType));
        } catch (JsonValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonValidationException(e);
        }
    }

    @Override
    public JsonOptional<?> fromAbsentJson(Type type) {
        getInner(type);
        return JsonOptional.ofNothing();
    }

    @Override
    public JsonElement toJson(JsonOptional<?> value, Type type) throws JsonGenerationException {
        throw new JsonGenerationException("found JsonOptional in a non-vanishable context");
    }

    @Override
    public Optional<JsonElement> toOptionalJson(JsonOptional<?> value, Type type) throws JsonGenerationException {
        Type innerType = getInner(type);
        if (value.isAbsent()) {
            return Optional.empty();
        }
        @SuppressWarnings("rawtypes") JsonTypeAdapter innerAdapter = registry.getTypeAdapter(innerType);
        try {
            //noinspection unchecked
            return Optional.of(innerAdapter.toJson(value.getValueOrNothingAsNull(), innerType));
        } catch (JsonGenerationException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonGenerationException(e);
        }

    }

    private Type getInner(Type outer) {
        return TypeUtil.expectSingleParameterizedType(outer, JsonOptional.class);
    }

}
