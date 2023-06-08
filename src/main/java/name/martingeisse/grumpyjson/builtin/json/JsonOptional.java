package name.martingeisse.grumpyjson.builtin.json;

import java.util.Objects;

/**
 * To get a property that can be absent, null, or something, use JsonOptional<JsonNullable<YourType>>.
 */
public final class JsonOptional<T> {

    private final T value;

    private JsonOptional(T value) {
        this.value = value;
    }

    public static <T> JsonOptional<T> ofValue(T value) {
        return new JsonOptional<>(Objects.requireNonNull(value, "value"));
    }

    public static <T> JsonOptional<T> ofNothing() {
        return new JsonOptional<>(null);
    }

    public T getValueOrNull() {
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    public boolean isAbsent() {
        return value == null;
    }

}
