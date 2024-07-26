/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import name.martingeisse.grumpyjson.FieldErrorNode;
import name.martingeisse.grumpyjson.JsonProviders;
import name.martingeisse.grumpyjson.JsonRegistries;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.json_model.JsonArray;
import name.martingeisse.grumpyjson.json_model.JsonElement;
import name.martingeisse.grumpyjson.registry.NotRegisteredException;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;
import name.martingeisse.grumpyjson.util.TypeUtil;

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
