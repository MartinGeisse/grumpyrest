package name.martingeisse.grumpyjson.builtin.json;

import java.util.Objects;

/**
 * To get a property that can be absent, null, or something, use JsonOptional<JsonNullable<YourType>>.
 */
public final class JsonNullable<T> {

    private final T value;

    private JsonNullable(T value) {
        this.value = value;
    }

    public static <T> JsonNullable<T> ofValue(T value) {
        return new JsonNullable<>(Objects.requireNonNull(value, "value"));
    }

    public static <T> JsonNullable<T> ofNull() {
        return new JsonNullable<>(null);
    }

    public static <T> JsonNullable<T> ofValueOrNull(T value) {
        return new JsonNullable<>(value);
    }

    public T getValueOrNull() {
        return value;
    }

    public T getValue() {
        if (value == null) {
            throw new IllegalStateException("this JsonNullable is null");
        }
        return value;
    }

    public boolean isNonNull() {
        return value != null;
    }

    public boolean isNull() {
        return value == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JsonNullable<?> that = (JsonNullable<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "JsonNullable{value=" + value + '}';
    }

}
