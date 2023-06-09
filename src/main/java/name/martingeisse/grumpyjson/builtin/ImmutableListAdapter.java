package name.martingeisse.grumpyjson.builtin;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.*;

import java.lang.reflect.Type;

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
        Type elementType = TypeUtil.expectSingleParameterizedType(type, ImmutableList.class);
        return null;
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
