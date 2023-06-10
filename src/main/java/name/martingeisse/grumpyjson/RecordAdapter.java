package name.martingeisse.grumpyjson;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.*;

/**
 * A RecordAdapter is build for the raw type (class) of a record, so a single RecordAdapter handles all parameterized
 * types for that raw type.
 *
 * At run time (potential optimization: at adapter selection time) the adapter is used for a concrete parameterized
 * type. This type must be concrete in the sense that it cannot contain type variables anymore (nor wildcards -- we
 * do not support those anyway). If the field that uses the record type *did* use type variables, then these must have
 * been replaced by concrete types before passing on to this adapter. So at this point the record type has an
 * ordered list of named type parameters, and the concrete type passed to use binds them to an ordered list of concrete
 * type arguments.
 *
 * This adapter then goes through the record fields. Each field potentially uses type variables, and all these must have
 * been declared as type parameters by the record -- type variables (as opposed to the types they are bound to) do not
 * cross the boundaries of a single record definition, and fields cannot define their own type variables. There is
 * actually a single exception (maybe) to this rule, and that is non-static inner classes, so we just don't support
 * those yet.
 *
 * For each record field, the field type is "concretized" -- replaced by a like-structured type in which type variables
 * have been replaced by the types they are bound to. A single type variable is looked up in the record's type
 * parameters by name, then the type argument at the same index is bound to the variable.
 */
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
