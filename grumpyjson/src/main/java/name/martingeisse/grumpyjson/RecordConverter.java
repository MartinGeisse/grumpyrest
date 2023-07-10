/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * A RecordAdapter is build for the raw type (class) of a record, so a single RecordAdapter handles all parameterized
 * types for that raw type.
 * <p>
 * At run time (potential optimization: at adapter selection time) the adapter is used for a concrete parameterized
 * type. This type must be concrete in the sense that it cannot contain type variables anymore (nor wildcards -- we
 * do not support those anyway). If the field that uses the record type *did* use type variables, then these must have
 * been replaced by concrete types before passing on to this adapter. So at this point the record type has an
 * ordered list of named type parameters, and the concrete type passed to use binds them to an ordered list of concrete
 * type arguments.
 * <p>
 * This adapter then goes through the record fields. Each field potentially uses type variables, and all these must have
 * been declared as type parameters by the record -- type variables (as opposed to the types they are bound to) do not
 * cross the boundaries of a single record definition, and fields cannot define their own type variables. There is
 * actually a single exception (maybe) to this rule, and that is non-static inner classes, so we just don't support
 * those yet.
 * <p>
 * For each record field, the field type is "concretized" -- replaced by a like-structured type in which type variables
 * have been replaced by the types they are bound to. A single type variable is looked up in the record's type
 * parameters by name, then the type argument at the same index is bound to the variable.
 *
 * @param <T> the record type
 */
public final class RecordConverter<T> implements JsonTypeAdapter<T> {

    private final RecordInfo recordInfo;
    private final JsonRegistry registry;

    RecordConverter(Class<T> clazz, JsonRegistry registry) {
        Objects.requireNonNull(clazz, "clazz");
        Objects.requireNonNull(registry, "registry");
        this.recordInfo = new RecordInfo(clazz);
        this.registry = registry;
    }

    @Override
    public boolean supportsType(Type type) {
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
    public T fromJson(JsonElement json, Type recordType) throws JsonValidationException {
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
                    @SuppressWarnings("rawtypes") JsonTypeAdapter adapter = registry.getTypeAdapter(concreteFieldType);
                    if (propertyJson == null) {
                        fieldValues[i] = adapter.fromAbsentJson(concreteFieldType);
                    } else {
                        fieldValues[i] = adapter.fromJson(propertyJson, concreteFieldType);
                    }
                } catch (JsonValidationException e) {
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
                throw new JsonValidationException(errorNode);
            }
            //noinspection unchecked
            return (T)recordInfo.invokeConstructor(fieldValues);
        }
        throw new JsonValidationException("expected object, found: " + json);
    }

    @Override
    public JsonElement toJson(T record, Type recordType) {
        Objects.requireNonNull(record, "value");
        Objects.requireNonNull(recordType, "recordType");
        JsonObject jsonObject = new JsonObject();
        FieldErrorNode errorNode = null;
        for (RecordInfo.ComponentInfo componentInfo : recordInfo.getComponentInfos()) {
            String name = componentInfo.getName();
            try {
                Object value = componentInfo.invokeGetter(record);
                if (value == null) {
                    throw new JsonGenerationException("field is null");
                }
                Type concreteFieldType = componentInfo.getConcreteType(recordType);
                @SuppressWarnings("rawtypes") JsonTypeAdapter adapter = registry.getTypeAdapter(concreteFieldType);
                @SuppressWarnings("unchecked") Optional<JsonElement> optionalJson = adapter.toOptionalJson(value, concreteFieldType);
                optionalJson.ifPresent(jsonElement -> jsonObject.add(name, jsonElement));
            } catch (JsonGenerationException e) {
                errorNode = e.getFieldErrorNode().in(name).and(errorNode);
            } catch (Exception e) {
                errorNode = FieldErrorNode.create(e).in(name).and(errorNode);
            }
        }
        if (errorNode != null) {
            throw new JsonGenerationException(errorNode);
        }
        return jsonObject;
    }

}
