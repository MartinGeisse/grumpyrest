/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import java.util.Objects;

/**
 * This can be used to wrap a property and allow JSON null instead of an actual value. Normally, JSON null
 * cannot be used even for properties which use reference types on the Java side.
 * <p>
 * A nullable field is not by itself optional, i.e. cannot be absent from the JSON. To get a property that can be
 * absent, null, or something, use OptionalField&lt;NullableField&lt;YourType&gt;&gt;.
 * <p>
 * A nullable field does not map to a null reference on the Java side, and using a null reference during JSON
 * serialization throws an exception. Instead, a JSON null is mapped to and from a NullableField that {@link #isNull()}.
 *
 * @param <T> the type of the contained value
 */
public final class NullableField<T> {

    private final T value;

    private NullableField(T value) {
        this.value = value;
    }

    /**
     * Creates a new instance that is non-null, i.e. has a value.
     *
     * @param value the value (must not be null)
     * @return the new instance
     * @param <T> the static type of the value
     */
    public static <T> NullableField<T> ofValue(T value) {
        return new NullableField<>(Objects.requireNonNull(value, "value"));
    }

    /**
     * Creates a new instance that is null.
     *
     * @return the new instance
     * @param <T> the static type of the missing value
     */
    public static <T> NullableField<T> ofNull() {
        return new NullableField<>(null);
    }

    /**
     * Creates a new instance that is non-null if the argument is non-null, and null if the argument is null.
     *
     * @param value the value or null
     * @return the new instance
     * @param <T> the static type of the value
     */
    public static <T> NullableField<T> ofValueOrNull(T value) {
        return new NullableField<>(value);
    }

    /**
     * Getter method for the value in this instance. Returns null if this instance is null.
     *
     * @return the value or null
     */
    public T getValueOrNull() {
        return value;
    }

    /**
     * Getter method for the value in this instance. Throws an {@link IllegalStateException} if null.
     *
     * @return the value
     */
    public T getValue() {
        if (value == null) {
            throw new IllegalStateException("this NullableField is null");
        }
        return value;
    }

    /**
     * Checks if this instance is non-null.
     *
     * @return true if this instance is non-null, false if null
     */
    public boolean isNonNull() {
        return value != null;
    }

    /**
     * Checks if this instance is null.
     *
     * @return true if this instance is null, false if non-null
     */
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
        NullableField<?> that = (NullableField<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "NullableField{value=" + value + '}';
    }

}
