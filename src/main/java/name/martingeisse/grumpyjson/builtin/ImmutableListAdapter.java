/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImmutableListAdapter implements JsonTypeAdapter<ImmutableList<?>> {

    private final JsonRegistry registry;

    public ImmutableListAdapter(JsonRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean supportsType(Type type) {
        return TypeUtil.isSingleParameterizedType(type, ImmutableList.class) != null;
    }

    @Override
    public ImmutableList<?> fromJson(JsonElement json, Type type) throws JsonValidationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        if (json instanceof JsonArray array) {
            Type elementType = TypeUtil.expectSingleParameterizedType(type, ImmutableList.class);
            @SuppressWarnings("rawtypes") JsonTypeAdapter elementTypeAdapter = registry.getTypeAdapter(elementType);
            List<Object> result = new ArrayList<>();
            FieldErrorNode errorNode = null;
            for (int i = 0; i < array.size(); i++) {
                try {
                    result.add(elementTypeAdapter.fromJson(array.get(i), elementType));
                } catch (JsonValidationException e) {
                    errorNode = e.fieldErrorNode.in(Integer.toString(i)).and(errorNode);
                } catch (Exception e) {
                    errorNode = FieldErrorNode.create(e).in(Integer.toString(i)).and(errorNode);
                }
            }
            if (errorNode != null) {
                throw new JsonValidationException(errorNode);
            }
            return ImmutableList.copyOf(result);
        }
        throw new JsonValidationException("expected int, found: " + json);
    }

    @Override
    public JsonElement toJson(ImmutableList<?> value, Type type) throws JsonGenerationException {
        Type elementType = TypeUtil.expectSingleParameterizedType(type, ImmutableList.class);
        @SuppressWarnings("rawtypes") JsonTypeAdapter elementTypeAdapter = registry.getTypeAdapter(elementType);
        JsonArray result = new JsonArray();
        FieldErrorNode errorNode = null;
        for (int i = 0; i < value.size(); i++) {
            try {
                //noinspection unchecked
                result.add(elementTypeAdapter.toJson(value.get(i), elementType));
            } catch (JsonGenerationException e) {
                errorNode = e.fieldErrorNode.in(Integer.toString(i)).and(errorNode);
            } catch (Exception e) {
                errorNode = FieldErrorNode.create(e).in(Integer.toString(i)).and(errorNode);
            }
        }
        if (errorNode != null) {
            throw new JsonGenerationException(errorNode);
        }
        return result;
    }
}
