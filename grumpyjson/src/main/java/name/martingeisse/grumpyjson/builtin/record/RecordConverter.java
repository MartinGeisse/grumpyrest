/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.record;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import name.martingeisse.grumpyjson.ExceptionMessages;
import name.martingeisse.grumpyjson.FieldErrorNode;
import name.martingeisse.grumpyjson.JsonRegistries;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * *** maps to JSON object ***
 * ...
 * type has an ordered list of named type parameters, and the concrete ***type +s*** *** ??? passed to use binds them to an ordered list
 * of concrete type arguments.
 * <p>
 *     ...
 *     *** finally,  the fields get deserialized from the JSON fields using the resulting concrete types ***
 *
 * @param <T> the record type
 */
public final class RecordConverter<T> implements JsonSerializer<T>, JsonDeserializer {

    private final RecordInfo recordInfo;
    private final JsonRegistries registries;

    RecordConverter(Class<T> clazz, JsonRegistries registries) {
        Objects.requireNonNull(clazz, "clazz");
        Objects.requireNonNull(registries, "registries");
        this.recordInfo = new RecordInfo(clazz);
        this.registries = registries;
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        Objects.requireNonNull(type, "type");
        if (type instanceof Class<?>) {
            return type.equals(recordInfo.getRecordClass());
        } else if (type instanceof ParameterizedType p && p.getRawType() instanceof Class<?>) {
            return p.getRawType().equals(recordInfo.getRecordClass());
        } else {
            return false;
        }
    }

    @Override
    public T deserialize(JsonElement json, Type recordType) throws JsonDeserializationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(recordType, "recordType");
        if (json instanceof JsonObject jsonObject) {
            List<RecordInfo.ComponentInfo> componentInfos = recordInfo.getComponentInfos();
            int numberOfPresentProperties = 0;
            Object[] fieldValues = new Object[componentInfos.size()];
            FieldErrorNode errorNode = null;

            for (int i = 0; i < componentInfos.size(); i++) {
                RecordInfo.ComponentInfo componentInfo = componentInfos.get(i);
                String name = componentInfo.getName();
                JsonElement propertyJson = jsonObject.get(name);
                if (propertyJson != null) {
                    numberOfPresentProperties++;
                }
                try {
                    Type concreteFieldType = componentInfo.getConcreteType(recordType);
                    JsonDeserializer deserializer = registries.getDeserializer(concreteFieldType);
                    if (propertyJson == null) {
                        fieldValues[i] = deserializer.deserializeAbsent(concreteFieldType);
                    } else {
                        fieldValues[i] = deserializer.deserialize(propertyJson, concreteFieldType);
                    }
                } catch (JsonDeserializationException e) {
                    errorNode = e.getFieldErrorNode().in(name).and(errorNode);
                } catch (Exception e) {
                    errorNode = FieldErrorNode.create(e).in(name).and(errorNode);
                }
            }

            if (numberOfPresentProperties != jsonObject.size()) {
                // this is more expensive, so only do this if there is really an error
                Set<String> propertyNames = new HashSet<>(jsonObject.keySet());
                for (RecordInfo.ComponentInfo componentInfo : componentInfos) {
                    propertyNames.remove(componentInfo.getName());
                }
                for (String unexpectedProperty : propertyNames) {
                    errorNode = FieldErrorNode.create(ExceptionMessages.UNEXPECTED_PROPERTY).in(unexpectedProperty).and(errorNode);
                }
            }

            if (errorNode != null) {
                throw new JsonDeserializationException(errorNode);
            }
            //noinspection unchecked
            return (T) recordInfo.invokeConstructor(fieldValues);
        }
        throw new JsonDeserializationException("expected object, found: " + json);
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        return clazz.equals(recordInfo.getRecordClass());
    }

    @Override
    public JsonElement serialize(T record) {
        Objects.requireNonNull(record, "value");
        JsonObject jsonObject = new JsonObject();
        FieldErrorNode errorNode = null;
        for (RecordInfo.ComponentInfo componentInfo : recordInfo.getComponentInfos()) {
            String name = componentInfo.getName();
            try {
                Object value = componentInfo.invokeGetter(record);
                if (value == null) {
                    throw new JsonSerializationException("field is null");
                }
                Optional<JsonElement> optionalJson = registries.serializeOptional(value);
                optionalJson.ifPresent(jsonElement -> jsonObject.add(name, jsonElement));
            } catch (JsonSerializationException e) {
                errorNode = e.getFieldErrorNode().in(name).and(errorNode);
            } catch (Exception e) {
                errorNode = FieldErrorNode.create(e).in(name).and(errorNode);
            }
        }
        if (errorNode != null) {
            throw new JsonSerializationException(errorNode);
        }
        return jsonObject;
    }

}
