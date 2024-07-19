/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * This can be used to wrap a property and allow it to be absent in JSON. Normally, a missing property in the JSON
 * will cause a deserialization error.
 * <p>
 * An optional field is not by itself nullable, i.e. cannot be JSON null. To get a property that can be absent, null,
 * or something, use OptionalField&lt;NullableField&lt;YourType&gt;&gt;.
 * <p>
 * An optional field does not map to a null reference on the Java side, and using a null reference during JSON
 * serialization throws an exception. Instead, a missing JSON property is mapped to and from an {@link OptionalField}
 * that {@link #isAbsent()}.
 *
 * @param <T> the type of the contained value
 */
public final class OptionalField<T> {

    private final T value;

    private OptionalField(T value) {
        this.value = value;
    }

    /**
     * Creates a new instance that is present, i.e. has a value.
     *
     * @param value the value (must not be null)
     * @return the new instance
     * @param <T> the static type of the value
     */
    public static <T> OptionalField<T> ofValue(T value) {
        return new OptionalField<>(Objects.requireNonNull(value, "value"));
    }

    /**
     * Creates a new instance that is absent.
     *
     * @return the new instance
     * @param <T> the static type of the missing value
     */
    public static <T> OptionalField<T> ofNothing() {
        return new OptionalField<>(null);
    }

    /**
     * Creates a new instance that is present if the argument is non-null, and absent if the argument is null.
     *
     * @param value the value or null
     * @return the new instance
     * @param <T> the static type of the value
     */
    public static <T> OptionalField<T> ofValueOrNullAsNothing(T value) {
        return new OptionalField<>(value);
    }

    /**
     * Getter method for the value in this instance. Returns null if this instance is absent.
     *
     * @return the value or null
     */
    public T getValueOrNothingAsNull() {
        return value;
    }

    /**
     * Getter method for the value in this instance. Throws an {@link IllegalStateException} if absent.
     *
     * @return the value
     */
    public T getValue() {
        if (value == null) {
            throw new IllegalStateException("this OptionalField is absent");
        }
        return value;
    }

    /**
     * Checks if this instance is present.
     *
     * @return true if this instance is present, false if absent
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Checks if this instance is absent.
     *
     * @return true if this instance is absent, false if present
     */
    public boolean isAbsent() {
        return value == null;
    }

    /**
     * Returns this field's value if {@link #isPresent()}, otherwise the argument.
     *
     * @param other the default to use if this field {@link #isAbsent()}
     * @return this value or the argument. Returns the null reference iff both this field {@link #isAbsent()} and the
     * argument is a null reference.
     */
    public T orElse(T other) {
        return value == null ? other : value;
    }

    /**
     * Returns this field's value if {@link #isPresent()}, otherwise a value obtained from the argument.
     *
     * @param other a supplier for the default to use if this field {@link #isAbsent()}. Must not be a null reference.
     * @return this value or the argument-provided value. Returns the null reference iff both this field
     * {@link #isAbsent()} and the argument provides a null reference.
     */
    public T orElseGet(Supplier<T> other) {
        Objects.requireNonNull(other, "other");
        return value == null ? other.get() : value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionalField<?> that = (OptionalField<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "OptionalField{value=" + value + '}';
    }

}
