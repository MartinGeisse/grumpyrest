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
import io.github.grumpystuff.grumpyjson.json_model.JsonElement;
import io.github.grumpystuff.grumpyjson.json_model.JsonObject;
import io.github.grumpystuff.grumpyjson.json_model.JsonString;
import io.github.grumpystuff.grumpyjson.registry.NotRegisteredException;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializationException;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializer;
import io.github.grumpystuff.grumpyjson.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This converter handles type Map&lt;...&gt; for deserialization, and the Map interface and its implementing
 * classes for serialization.
 * <p>
 * This converter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clear()}.
 */
public final class MapConverter implements JsonSerializer<Map<?, ?>>, JsonDeserializer {

    private final JsonProviders providers;

    /**
     * Constructor.
     *
     * @param providers the JSON providers -- needed to fetch the converters for the key and value types at run-time
     */
    public MapConverter(JsonProviders providers) {
        this.providers = providers;
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        Objects.requireNonNull(type, "type");

        return TypeUtil.isParameterizedType(type, Map.class, 2) != null;
    }

    private static String buildFromJsonFieldName(boolean isAtKey, String key) {
        return (isAtKey ? "key[" : "value[") + key + "]";
    }

    @Override
    public Map<?, ?> deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        Map<String, JsonElement> jsonProperties = json.deserializerExpectsObject();
        Type[] keyAndValueTypes = TypeUtil.expectParameterizedType(type, List.class, 2);
        Type keyType = keyAndValueTypes[0];
        Type valueType = keyAndValueTypes[1];
        JsonDeserializer keyDeserializer, valueDeserializer;
        try {
            keyDeserializer = providers.getDeserializer(keyType);
            valueDeserializer = providers.getDeserializer(valueType);
        } catch (NotRegisteredException e) {
            throw new JsonDeserializationException(e.getMessage());
        }
        Map<Object, Object> result = new HashMap<>();
        FieldErrorNode errorNode = null;
        for (Map.Entry<String, JsonElement> entry : jsonProperties.entrySet()) {
            String keyText = entry.getKey();
            boolean isAtKey = true;
            try {
                Object key = keyDeserializer.deserialize(JsonString.of(keyText), keyType);
                isAtKey = false;
                Object value = valueDeserializer.deserialize(entry.getValue(), valueType);
                result.put(key, value);
            } catch (JsonDeserializationException e) {
                errorNode = e.getFieldErrorNode().in(buildFromJsonFieldName(isAtKey, keyText)).and(errorNode);
            } catch (Exception e) {
                errorNode = FieldErrorNode.create(e).in(buildFromJsonFieldName(isAtKey, keyText)).and(errorNode);
            }
        }
        if (errorNode != null) {
            throw new JsonDeserializationException(errorNode);
        }
        return Map.copyOf(result);
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz");

        return Map.class.isAssignableFrom(clazz);
    }

    @Override
    public JsonElement serialize(Map<?, ?> map) throws JsonSerializationException {
        Objects.requireNonNull(map, "value"); // called value in the interface

        Map<String, JsonElement> jsonProperties = new HashMap<>();
        FieldErrorNode errorNode = null;
        for (Map.Entry<?, ?> entry : map.entrySet()) {

            // handle key
            Object keyObject = entry.getKey();
            if (keyObject == null) {
                throw new JsonSerializationException("map contains null key");
            }
            JsonElement keyJson;
            try {
                keyJson = providers.serialize(keyObject);
            } catch (JsonSerializationException e) {
                errorNode = e.getFieldErrorNode().in("[" + keyObject + "]").and(errorNode);
                continue;
            } catch (Exception e) {
                errorNode = FieldErrorNode.create(e).in("[" + keyObject + "]").and(errorNode);
                continue;
            }
            String keyText;
            if (keyJson instanceof JsonString s) {
                keyText = s.getValue();
            } else {
                errorNode = FieldErrorNode.create("map key does not serialize to a JSON string")
                    .in("[" + keyObject + "]")
                    .and(errorNode);
                continue;
            }
            try {
                // handle value
                Object valueObject = entry.getValue();
                if (valueObject == null) {
                    throw new JsonSerializationException("map contains null value");
                }
                JsonElement valueJson = providers.serialize(valueObject);
                jsonProperties.put(keyText, valueJson);

            } catch (JsonSerializationException e) {
                errorNode = e.getFieldErrorNode().in(keyText).and(errorNode);
            } catch (Exception e) {
                errorNode = FieldErrorNode.create(e).in(keyText).and(errorNode);
            }
        }
        if (errorNode != null) {
            throw new JsonSerializationException(errorNode);
        }
        return JsonObject.of(jsonProperties);
    }
}
