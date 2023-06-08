package name.martingeisse.grumpyjson;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.Objects;

public final class RecordAdapter<T> implements JsonTypeAdapter<T> {

    @SuppressWarnings({"rawtypes", "unchecked"})
    private record ComponentAdapter(RecordComponent component, JsonTypeAdapter typeAdapter) {

        public void toJson(Object javaContainer, JsonObject jsonContainer) {
            Method accessor = component.getAccessor();
            Object componentValue;
            try {
                componentValue = accessor.invoke(javaContainer);
            } catch (Exception e) {
                throw new JsonGenerationException("could not invoke getter " + accessor + " on " + javaContainer);
            }
            if (componentValue == null) {
                throw JsonGenerationException.fieldIsNull();
            }
            jsonContainer.add(component.getName(), typeAdapter.toJson(componentValue, component.getGenericType()));
        }

    }

    private final Class<T> clazz;
    private final Constructor<?> constructor;
    private final ImmutableList<ComponentAdapter> componentAdapters;

    public RecordAdapter(Class<T> clazz, JsonRegistry registry) {
        Objects.requireNonNull(clazz, "clazz");
        Objects.requireNonNull(registry, "registry");

        if (!clazz.isRecord()) {
            throw new IllegalArgumentException("not a record: " + clazz);
        }
        this.clazz = clazz;

        RecordComponent[] components = clazz.getRecordComponents();
        Class<?>[] rawComponentTypes = new Class<?>[components.length];
        ComponentAdapter[] componentAdapters = new ComponentAdapter[components.length];
        for (int i = 0; i < components.length; i++) {
            RecordComponent component = components[i];
            rawComponentTypes[i] = component.getType();
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
            JsonTypeAdapter<?> componentTypeAdapter = registry.getTypeAdapter(component.getGenericType());
            componentAdapters[i] = new ComponentAdapter(component, componentTypeAdapter);
        }
        try {
            this.constructor = clazz.getDeclaredConstructor(rawComponentTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("could not find canonical constructor for record type " + clazz);
        }
        this.componentAdapters = ImmutableList.copyOf(componentAdapters);
    }

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        return type.equals(clazz);
    }

    @Override
    public T fromJson(JsonElement json, Type type) throws JsonValidationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");

        throw new UnsupportedOperationException();
    }

    @Override
    public JsonElement toJson(T value, Type type) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");

        JsonObject result = new JsonObject();
        for (ComponentAdapter adapter : componentAdapters) {
            try {
                adapter.toJson(value, result);
            } catch (JsonGenerationException e) {
                e.getReverseStackAccumulator().add(adapter.component.getName());
                throw e;
            }
        }
        return result;
    }

}
