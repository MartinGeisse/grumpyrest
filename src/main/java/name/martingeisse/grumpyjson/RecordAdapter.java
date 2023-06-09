package name.martingeisse.grumpyjson;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.*;

public final class RecordAdapter<T> implements JsonTypeAdapter<T> {

    @SuppressWarnings({"rawtypes", "unchecked"})
    private record ComponentAdapter(RecordInfo.ComponentInfo componentInfo, JsonTypeAdapter typeAdapter) {

        public void toJson(Object javaContainer, JsonObject jsonContainer) {
            String name = componentInfo.getName();
            try {
                Object value = componentInfo.invokeGetter(javaContainer);
                if (value == null) {
                    throw new JsonGenerationException("field is null");
                }
                Optional<JsonElement> optionalJson = typeAdapter.toOptionalJson(value, componentInfo.getType());
                optionalJson.ifPresent(jsonElement -> jsonContainer.add(name, jsonElement));
            } catch (JsonGenerationException e) {
                e.fieldErrorNode = e.fieldErrorNode.in(name);
                throw e;
            } catch (Exception e) {
                throw new JsonGenerationException(e);
            }
        }

        // returns null if absent
        public JsonElement getFromJsonContainer(JsonObject jsonContainer) {
            return jsonContainer.get(componentInfo().getName());
        }

        public Object convertFromJson(JsonElement propertyJson) throws JsonValidationException {
            String name = componentInfo.getName();
            try {
                Type type = componentInfo.getType();
                return propertyJson == null ? typeAdapter.fromAbsentJson(type) : typeAdapter.fromJson(propertyJson, type);
            } catch (JsonValidationException e) {
                e.fieldErrorNode = e.fieldErrorNode.in(name);
                throw e;
            }
        }

    }

    private final RecordInfo<T> recordInfo;
    private final ImmutableList<ComponentAdapter> componentAdapterList;

    public RecordAdapter(Class<T> clazz, JsonRegistry registry) {
        Objects.requireNonNull(clazz, "clazz");
        Objects.requireNonNull(registry, "registry");
        this.recordInfo = new RecordInfo<>(clazz);

        List<ComponentAdapter> componentAdapterList = new ArrayList<>();
        for (RecordInfo.ComponentInfo componentInfo : recordInfo.getComponentInfos()) {
            //
            // This works for fields with fully-bound generic types, but will not pass any type parameter bindings
            // from the record down to its components. IOW, this will work:
            //
            //   record Foo {List<String> field}
            //
            // when parsing a Foo record, but this won't
            //
            //   record Foo<T> {List<T> field}
            //
            // when parsing a Foo<String> record, because the current implementation will not corrently pass down the
            // binding T->String to the field, and instead look up an adapter for "T" in the registry, which obviously
            // does not exist. Fixing this is not fundamentally a hard problem, but a bit tricky to get right due
            // to Java's weird way to handle generics, and might also require a distinction between records with/without
            // type parameters, and in records with TPs, a distinction between fields that use type variables vs. those
            // that don't, to avoid performance degradation when treating everything like the most complex possible case.
            //
            JsonTypeAdapter<?> componentTypeAdapter = registry.getTypeAdapter(componentInfo.component().getGenericType());
            componentAdapterList.add(new ComponentAdapter(componentInfo, componentTypeAdapter));
        }
        this.componentAdapterList = ImmutableList.copyOf(componentAdapterList);
    }

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        return type.equals(recordInfo.getRecord());
    }

    @Override
    public T fromJson(JsonElement json, Type type) throws JsonValidationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        if (json instanceof JsonObject jsonObject) {
            int numberOfPresentProperties = 0;
            Object[] fieldValues = new Object[componentAdapterList.size()];
            FieldErrorNode errorNode = null;

            for (int i = 0; i < componentAdapterList.size(); i++) {
                ComponentAdapter componentAdapter = componentAdapterList.get(i);
                JsonElement propertyJson = componentAdapter.getFromJsonContainer(jsonObject);
                if (propertyJson != null) {
                    numberOfPresentProperties++;
                }
                try {
                    fieldValues[i] = componentAdapter.convertFromJson(propertyJson);
                } catch (JsonValidationException e) {
                    errorNode = e.fieldErrorNode.and(errorNode);
                }
            }

            if (numberOfPresentProperties != jsonObject.size()) {
                // this is more expensive, so only do this if there is really an error
                Set<String> propertyNames = new HashSet<>(jsonObject.keySet());
                for (ComponentAdapter componentAdapter : componentAdapterList) {
                    propertyNames.remove(componentAdapter.componentInfo().getName());
                }
                for (String unexpectedProperty : propertyNames) {
                    errorNode = FieldErrorNode.create(ExceptionMessages.UNEXPECTED_PROPERTY).in(unexpectedProperty).and(errorNode);
                }
            }

            if (errorNode != null) {
                throw new JsonValidationException(errorNode);
            }
            return recordInfo.invokeConstructor(fieldValues);
        }
        throw new JsonValidationException("expected object, found: " + json);
    }

    @Override
    public JsonElement toJson(T value, Type type) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        JsonObject result = new JsonObject();
        FieldErrorNode errorNode = null;
        for (ComponentAdapter adapter : componentAdapterList) {
            try {
                adapter.toJson(value, result);
            } catch (JsonGenerationException e) {
                errorNode = e.fieldErrorNode.and(errorNode);
            }
        }
        if (errorNode != null) {
            throw new JsonGenerationException(errorNode);
        }
        return result;
    }

}
