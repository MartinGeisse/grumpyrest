package name.martingeisse.grumpyjson;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;

public final class RecordAdapter<T> implements JsonTypeAdapter<T> {

    @SuppressWarnings({"rawtypes", "unchecked"})
    private record ComponentAdapter(RecordComponent component, TypeToken typeToken, JsonTypeAdapter typeAdapter) {

        public void toJson(Object javaContainer, JsonObject jsonContainer) {
            Method accessor = component.getAccessor();
            Object componentValue;
            try {
                componentValue = accessor.invoke(javaContainer);
            } catch (Exception e) {
                throw new JsonGenerationException("could not invoke getter " + accessor + " on " + javaContainer);
            }
            if (componentValue == null) {
                throw new JsonGenerationException("field value is null in toJson()");
            }
            jsonContainer.add(component.getName(), typeAdapter.toJson(componentValue, typeToken));
        }

    }

    private final Class<T> clazz;
    private final Constructor<?> constructor;
    private final ImmutableList<ComponentAdapter> componentAdapters;

    public RecordAdapter(Class<T> clazz, JsonRegistry registry) {
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
            JsonTypeAdapter<?> componentTypeAdapter = registry.getTypeAdapter(TypeToken.get(component.getGenericType()));
            componentAdapters[i] = new ComponentAdapter(component, TypeToken.get(component.getGenericType()), componentTypeAdapter);
        }
        try {
            this.constructor = clazz.getDeclaredConstructor(rawComponentTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("could not find canonical constructor for record type " + clazz);
        }
        this.componentAdapters = ImmutableList.copyOf(componentAdapters);
    }

    @Override
    public T fromJson(JsonElement json, TypeToken<? super T> type) throws JsonValidationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonElement toJson(T value, TypeToken<? super T> type) {
        if (value == null) {
            throw JsonGenerationException.fieldIsNull();
        }
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
