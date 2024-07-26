/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.builtin;

import io.github.grumpystuff.grumpyjson.FieldErrorNode;
import io.github.grumpystuff.grumpyjson.JsonProviders;
import io.github.grumpystuff.grumpyjson.JsonRegistries;
import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializationException;
import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializer;
import io.github.grumpystuff.grumpyjson.json_model.JsonArray;
import io.github.grumpystuff.grumpyjson.json_model.JsonElement;
import io.github.grumpystuff.grumpyjson.registry.NotRegisteredException;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializationException;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializer;
import io.github.grumpystuff.grumpyjson.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This converter handles type List&lt;...&gt; for deserialization, and the List interface and its implementing
 * classes for serialization.
 * <p>
 * This converter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clear()}.
 */
public final class ListConverter implements JsonSerializer<List<?>>, JsonDeserializer {

    private final JsonProviders providers;

    /**
     * Constructor.
     *
     * @param providers the JSON providers -- needed to fetch the converter for the element type at run-time
     */
    public ListConverter(JsonProviders providers) {
        Objects.requireNonNull(providers, "providers");

        this.providers = providers;
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        Objects.requireNonNull(type, "type");

        return TypeUtil.isSingleParameterizedType(type, List.class) != null;
    }

    @Override
    public List<?> deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        List<JsonElement> jsonChildren = json.deserializerExpectsArray();
        Type elementType = TypeUtil.expectSingleParameterizedType(type, List.class);
        JsonDeserializer elementDeserializer;
        try {
            elementDeserializer = providers.getDeserializer(elementType);
        } catch (NotRegisteredException e) {
            throw new JsonDeserializationException(e.getMessage());
        }
        List<Object> resultChildren = new ArrayList<>();
        FieldErrorNode errorNode = null;
        for (int i = 0; i < jsonChildren.size(); i++) {
            try {
                resultChildren.add(elementDeserializer.deserialize(jsonChildren.get(i), elementType));
            } catch (JsonDeserializationException e) {
                errorNode = e.getFieldErrorNode().in(Integer.toString(i)).and(errorNode);
            } catch (Exception e) {
                errorNode = FieldErrorNode.create(e).in(Integer.toString(i)).and(errorNode);
            }
        }
        if (errorNode != null) {
            throw new JsonDeserializationException(errorNode);
        }
        return List.copyOf(resultChildren);
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");

        return List.class.isAssignableFrom(clazz);
    }

    @Override
    public JsonElement serialize(List<?> value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");

        List<JsonElement> jsonChildren = new ArrayList<>();
        FieldErrorNode errorNode = null;
        for (int i = 0; i < value.size(); i++) {
            try {
                jsonChildren.add(providers.serialize(value.get(i)));
            } catch (JsonSerializationException e) {
                errorNode = e.getFieldErrorNode().in(Integer.toString(i)).and(errorNode);
            } catch (Exception e) {
                errorNode = FieldErrorNode.create(e).in(Integer.toString(i)).and(errorNode);
            }
        }
        if (errorNode != null) {
            throw new JsonSerializationException(errorNode);
        }
        return JsonArray.of(jsonChildren);
    }

}
