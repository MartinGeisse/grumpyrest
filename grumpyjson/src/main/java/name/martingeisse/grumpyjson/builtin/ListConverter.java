/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.*;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This adapter handles type List&lt;...&gt;. It does not handle subclasses such as ArrayList or ImmutableList
 * because there is no generic way to create instances of such classes while parsing, and the added value of having
 * such types in simple data transfer classes isn't that great anyway.
 */
public class ListConverter implements JsonTypeAdapter<List<?>> {

    private final JsonRegistry registry;

    /**
     * Constructor.
     *
     * @param registry the JSON registry -- needed to fetch the adapter for the element type at run-time
     */
    public ListConverter(JsonRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean supportsType(Type type) {
        return TypeUtil.isSingleParameterizedType(type, List.class) != null;
    }

    @Override
    public List<?> deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        if (json instanceof JsonArray array) {
            Type elementType = TypeUtil.expectSingleParameterizedType(type, List.class);
            @SuppressWarnings("rawtypes") JsonTypeAdapter elementTypeAdapter = registry.getTypeAdapter(elementType);
            List<Object> result = new ArrayList<>();
            FieldErrorNode errorNode = null;
            for (int i = 0; i < array.size(); i++) {
                try {
                    result.add(elementTypeAdapter.deserialize(array.get(i), elementType));
                } catch (JsonDeserializationException e) {
                    errorNode = e.getFieldErrorNode().in(Integer.toString(i)).and(errorNode);
                } catch (Exception e) {
                    errorNode = FieldErrorNode.create(e).in(Integer.toString(i)).and(errorNode);
                }
            }
            if (errorNode != null) {
                throw new JsonDeserializationException(errorNode);
            }
            return List.copyOf(result);
        }
        throw new JsonDeserializationException("expected int, found: " + json);
    }

    @Override
    public JsonElement serialize(List<?> value, Type type) throws JsonSerializationException {
        Type elementType = TypeUtil.expectSingleParameterizedType(type, List.class);
        @SuppressWarnings("rawtypes") JsonTypeAdapter elementTypeAdapter = registry.getTypeAdapter(elementType);
        JsonArray result = new JsonArray();
        FieldErrorNode errorNode = null;
        for (int i = 0; i < value.size(); i++) {
            try {
                //noinspection unchecked
                result.add(elementTypeAdapter.serialize(value.get(i), elementType));
            } catch (JsonSerializationException e) {
                errorNode = e.getFieldErrorNode().in(Integer.toString(i)).and(errorNode);
            } catch (Exception e) {
                errorNode = FieldErrorNode.create(e).in(Integer.toString(i)).and(errorNode);
            }
        }
        if (errorNode != null) {
            throw new JsonSerializationException(errorNode);
        }
        return result;
    }
}
