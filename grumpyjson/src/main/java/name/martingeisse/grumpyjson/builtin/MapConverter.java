/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import name.martingeisse.grumpyjson.*;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.registry.NotRegisteredException;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;
import name.martingeisse.grumpyjson.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.*;

/**
 * This converter handles type Map&lt;...&gt; for deserialization, and the Map interface and its implementing
 * classes for serialization.
 * <p>
 * This converter is registered by default, and only needs to be manually registered if it gets removed, such as by
 * calling {@link JsonRegistries#clear()}.
 */
public final class MapConverter implements JsonSerializer<Map<?, ?>>, JsonDeserializer {

    private final JsonRegistries registries;

    /**
     * Constructor.
     *
     * @param registries the JSON registries -- needed to fetch the converters for the key and value types at run-time
     */
    public MapConverter(JsonRegistries registries) {
        this.registries = registries;
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        return TypeUtil.isParameterizedType(type, Map.class, 2) != null;
    }

    private static String buildFromJsonFieldName(boolean isAtKey, String key) {
        return (isAtKey ? "key[" : "value[") + key + "]";
    }

    @Override
    public Map<?, ?> deserialize(JsonElement json, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        if (json instanceof JsonObject map) {
            Type[] keyAndValueTypes = TypeUtil.expectParameterizedType(type, List.class, 2);
            Type keyType = keyAndValueTypes[0];
            Type valueType = keyAndValueTypes[1];
            JsonDeserializer keyDeserializer, valueDeserializer;
            try {
                keyDeserializer = registries.getDeserializer(keyType);
                valueDeserializer = registries.getDeserializer(valueType);
            } catch (NotRegisteredException e) {
                throw new JsonDeserializationException(e.getMessage());
            }
            Map<Object, Object> result = new HashMap<>();
            FieldErrorNode errorNode = null;
            for (Map.Entry<String, JsonElement> entry : map.entrySet()) {
                String keyText = entry.getKey();
                boolean isAtKey = true;



                try {
                    Object key = keyTypeAdapter.fromJson(new JsonPrimitive(keyText), keyType);
                    isAtKey = false;
                    Object value = valueTypeAdapter.fromJson(entry.getValue(), valueType);
                    result.put(key, value);
                } catch (JsonValidationException e) {
                    errorNode = e.getFieldErrorNode().in(buildFromJsonFieldName(isAtKey, keyText)).and(errorNode);
                } catch (Exception e) {
                    errorNode = FieldErrorNode.create(e).in(buildFromJsonFieldName(isAtKey, keyText)).and(errorNode);
                }




                try {
                    result.add(elementDeserializer.deserialize(array.get(i), elementType));
                } catch (JsonDeserializationException e) {
                    errorNode = e.getFieldErrorNode().in(Integer.toString(i)).and(errorNode);
                } catch (Exception e) {
                    errorNode = FieldErrorNode.create(e).in(Integer.toString(i)).and(errorNode);
                }



            }
            if (errorNode != null) {
                throw new JsonDeserializationException(errorNode);
            }
            return Map.copyOf(result);
        }
        throw new JsonDeserializationException("expected object, found: " + json);
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    @Override
    public JsonElement serialize(Map<?, ?> value) throws JsonSerializationException {
        JsonObject result = new JsonObject();
        FieldErrorNode errorNode = null;



        for (int i = 0; i < value.size(); i++) {
            try {
                result.add(registries.serialize(value.get(i)));
            } catch (JsonSerializationException e) {
                errorNode = e.getFieldErrorNode().in(Integer.toString(i)).and(errorNode);
            } catch (Exception e) {
                errorNode = FieldErrorNode.create(e).in(Integer.toString(i)).and(errorNode);
            }
        }






        for (Map.Entry<?, ?> entry : map.entrySet()) {

            // handle key
            Object keyObject = entry.getKey();
            if (keyObject == null) {
                throw new JsonGenerationException("map contains null key");
            }
            JsonElement keyJson;
            try {
                @SuppressWarnings("rawtypes") JsonTypeAdapter adapter = registry.getTypeAdapter(keyObject.getClass());
                //noinspection unchecked
                keyJson = adapter.toJson(keyObject, keyObject.getClass());
            } catch (JsonGenerationException e) {
                errorNode = e.getFieldErrorNode().in("[" + keyObject + "]").and(errorNode);
                continue;
            } catch (Exception e) {
                errorNode = FieldErrorNode.create(e).in("[" + keyObject + "]").and(errorNode);
                continue;
            }
            if (!(keyJson instanceof JsonPrimitive p) || !p.isString()) {
                errorNode = FieldErrorNode.create("map key does not serialize to a JSON string")
                    .in("[" + keyObject + "]")
                    .and(errorNode);
                continue;
            }
            String keyText = keyJson.getAsString();
            try {

                // handle value
                Object valueObject = entry.getValue();
                if (valueObject == null) {
                    throw new JsonGenerationException("map contains null value");
                }
                @SuppressWarnings("rawtypes") JsonTypeAdapter adapter = registry.getTypeAdapter(valueObject.getClass());
                //noinspection unchecked
                JsonElement valueJson = adapter.toJson(valueObject, valueObject.getClass());
                result.add(keyText, valueJson);

            } catch (JsonGenerationException e) {
                errorNode = e.getFieldErrorNode().in(keyText).and(errorNode);
            } catch (Exception e) {
                errorNode = FieldErrorNode.create(e).in(keyText).and(errorNode);
            }
        }






        if (errorNode != null) {
            throw new JsonSerializationException(errorNode);
        }
        return result;
    }
}
