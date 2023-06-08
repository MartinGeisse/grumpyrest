package name.martingeisse.grumpyjson;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

public interface JsonTypeAdapter<T> {
    T fromJson(JsonElement json, TypeToken<? super T> type) throws JsonValidationException;
    JsonElement toJson(T value, TypeToken<? super T> type) throws JsonGenerationException;
}
