package name.martingeisse.grumpyjson.builtin;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.JsonGenerationException;
import name.martingeisse.grumpyjson.JsonRegistry;
import name.martingeisse.grumpyjson.JsonTypeAdapter;
import name.martingeisse.grumpyjson.JsonValidationException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ImmutableListAdapter implements JsonTypeAdapter<ImmutableList<?>> {

    private final JsonRegistry registry;

    public ImmutableListAdapter(JsonRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean supportsType(Type type) {
        return (type instanceof ParameterizedType p) &&
                p.getRawType().equals(ImmutableList.class) &&
                p.getActualTypeArguments().length == 1;
    }

    @Override
    public ImmutableList<?> fromJson(JsonElement json, Type type) throws JsonValidationException {
        Type elementType = ((ParameterizedType)type).getActualTypeArguments()[0];
        return null;
    }

    @Override
    public JsonElement toJson(ImmutableList<?> value, Type type) throws JsonGenerationException {
        Type elementType = ((ParameterizedType)type).getActualTypeArguments()[0];
        @SuppressWarnings("rawtypes") JsonTypeAdapter elementTypeAdapter = registry.getTypeAdapter(elementType);
        JsonArray result = new JsonArray();
        for (Object element : value) {
            //noinspection unchecked
            result.add(elementTypeAdapter.toJson(element, elementType));
        }
        return result;
    }
}
