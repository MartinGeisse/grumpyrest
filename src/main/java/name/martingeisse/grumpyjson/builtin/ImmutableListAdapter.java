package name.martingeisse.grumpyjson.builtin;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImmutableListAdapter implements JsonTypeAdapter<ImmutableList<?>> {

    private final JsonRegistry registry;

    public ImmutableListAdapter(JsonRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean supportsType(Type type) {
        return TypeUtil.isSingleParameterizedType(type, ImmutableList.class) != null;
    }

    @Override
    public ImmutableList<?> fromJson(JsonElement json, Type type) throws JsonValidationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        if (json instanceof JsonArray array) {
            Type elementType = TypeUtil.expectSingleParameterizedType(type, ImmutableList.class);
            @SuppressWarnings("rawtypes") JsonTypeAdapter elementTypeAdapter = registry.getTypeAdapter(elementType);
            List<Object> result = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                try {
                    result.add(elementTypeAdapter.fromJson(array.get(i), elementType));
                } catch (JsonValidationException e) {
                    e.getReverseStackAccumulator().add(Integer.toString(i));
                    throw e;
                } catch (Exception e) {
                    throw new JsonValidationException(e);
                }
            }
            return ImmutableList.copyOf(result);
        }
        throw new JsonValidationException("expected int, found: " + json);
    }

    @Override
    public JsonElement toJson(ImmutableList<?> value, Type type) throws JsonGenerationException {
        Type elementType = TypeUtil.expectSingleParameterizedType(type, ImmutableList.class);
        @SuppressWarnings("rawtypes") JsonTypeAdapter elementTypeAdapter = registry.getTypeAdapter(elementType);
        JsonArray result = new JsonArray();
        for (int i = 0; i < value.size(); i++) {
            try {
                //noinspection unchecked
                result.add(elementTypeAdapter.toJson(value.get(i), elementType));
            } catch (JsonGenerationException e) {
                e.getReverseStackAccumulator().add(Integer.toString(i));
                throw e;
            } catch (Exception e) {
                throw new JsonGenerationException(e);
            }
        }
        return result;
    }
}
