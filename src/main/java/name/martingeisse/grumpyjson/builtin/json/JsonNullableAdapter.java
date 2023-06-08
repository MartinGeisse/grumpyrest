package name.martingeisse.grumpyjson.builtin.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import name.martingeisse.grumpyjson.*;

import java.lang.reflect.Type;

public class JsonNullableAdapter implements JsonTypeAdapter<JsonNullable<?>> {

    private final JsonRegistry registry;

    public JsonNullableAdapter(JsonRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean supportsType(Type type) {
        return TypeUtil.isSingleParameterizedType(type, JsonNullable.class) != null;
    }

    @Override
    public JsonNullable<?> fromJson(JsonElement json, Type type) throws JsonValidationException {
        Type innerType = getInner(type);
        if (json.isJsonNull()) {
            return JsonNullable.ofNull();
        }
        JsonTypeAdapter<?> innerAdapter = registry.getTypeAdapter(innerType);
        return JsonNullable.ofValue(innerAdapter.fromJson(json, innerType));
    }

    @Override
    public JsonElement toJson(JsonNullable<?> value, Type type) throws JsonGenerationException {
        Type innerType = getInner(type);
        if (value.isNull()) {
            return JsonNull.INSTANCE;
        }
        @SuppressWarnings("rawtypes") JsonTypeAdapter innerAdapter = registry.getTypeAdapter(innerType);
        //noinspection unchecked
        return innerAdapter.toJson(value.getValueOrNull(), innerType);
    }

    private Type getInner(Type outer) {
        return TypeUtil.expectSingleParameterizedType(outer, JsonNullable.class);
    }

}
