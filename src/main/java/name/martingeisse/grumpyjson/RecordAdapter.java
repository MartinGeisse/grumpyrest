package name.martingeisse.grumpyjson;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RecordAdapter<T> implements JsonTypeAdapter<T> {

    @SuppressWarnings({"rawtypes", "unchecked"})
    private record ComponentAdapter(RecordInfo.ComponentInfo componentInfo, JsonTypeAdapter typeAdapter) {
        public void toJson(Object javaContainer, JsonObject jsonContainer) {
            String name = componentInfo.getName();
            try {
                Object value = componentInfo.invokeGetter(javaContainer);
                if (value == null) {
                    throw JsonGenerationException.fieldIsNull();
                }
                JsonElement json = typeAdapter.toJson(value, componentInfo.getType());
                jsonContainer.add(name, json);
            } catch (JsonGenerationException e) {
                e.getReverseStackAccumulator().add(name);
                throw e;
            }
        }
    }

    private final RecordInfo<T> recordInfo;
    private final ImmutableList<ComponentAdapter> componentAdapters;

    public RecordAdapter(Class<T> clazz, JsonRegistry registry) {
        Objects.requireNonNull(clazz, "clazz");
        Objects.requireNonNull(registry, "registry");
        this.recordInfo = new RecordInfo<>(clazz);

        List<ComponentAdapter> componentAdapters = new ArrayList<>();
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
            componentAdapters.add(new ComponentAdapter(componentInfo, componentTypeAdapter));
        }
        this.componentAdapters = ImmutableList.copyOf(componentAdapters);
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

        throw new UnsupportedOperationException();
    }

    @Override
    public JsonElement toJson(T value, Type type) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        JsonObject result = new JsonObject();
        for (ComponentAdapter adapter : componentAdapters) {
            adapter.toJson(value, result);
        }
        return result;
    }

}
