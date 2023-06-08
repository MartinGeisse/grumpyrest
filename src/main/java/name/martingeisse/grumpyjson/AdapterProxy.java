package name.martingeisse.grumpyjson;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.Objects;

public final class AdapterProxy<T> implements JsonTypeAdapter<T> {

    private JsonTypeAdapter<T> target;

    public void setTarget(JsonTypeAdapter<T> target) {
        this.target = Objects.requireNonNull(target);
    }

    private JsonTypeAdapter<T> needTarget() {
        if (target == null) {
            throw new IllegalStateException("using an AdapterProxy before its target has been set");
        }
        return target;
    }

    @Override
    public boolean supportsType(TypeToken<?> type) {
        Objects.requireNonNull(type, "type");
        return needTarget().supportsType(type);
    }

    @Override
    public T fromJson(JsonElement json, TypeToken<? super T> type) throws JsonValidationException {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(type, "type");
        return needTarget().fromJson(json, type);
    }

    @Override
    public JsonElement toJson(T value, TypeToken<? super T> type) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(type, "type");
        return needTarget().toJson(value, type);
    }
}
