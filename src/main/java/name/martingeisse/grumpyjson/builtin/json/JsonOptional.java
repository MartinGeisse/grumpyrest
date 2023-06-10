/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
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

    public static <T> JsonOptional<T> ofValueOrNullAsNothing(T value) {
        return new JsonOptional<>(value);
    }

    public T getValueOrNothingAsNull() {
        return value;
    }

    public T getValue() {
        if (value == null) {
            throw new IllegalStateException("this JsonOptional is absent");
        }
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    public boolean isAbsent() {
        return value == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonOptional<?> that = (JsonOptional<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "JsonOptional{value=" + value + '}';
    }

}
