/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import java.util.Objects;

/**
 * This can be used to wrap a property and allow it to be absent in JSON. Normally, a missing property in the JSON
 * will cause a validation error.
 * <p>
 * An optional field is not by itself nullable, i.e. cannot be JSON null. To get a property that can be absent, null,
 * or something, use OptionalField&lt;NullableField&lt;YourType&gt;&gt;.
 * <p>
 * An optional field does not map to a null reference on the Java side, and using a null reference during JSON
 * generation throws an exception. Instead, a missing JSON property is mapped to and from an {@link OptionalField}
 * that {@link #isAbsent()}.
 */
public final class OptionalField<T> {

    private final T value;

    private OptionalField(T value) {
        this.value = value;
    }

    public static <T> OptionalField<T> ofValue(T value) {
        return new OptionalField<>(Objects.requireNonNull(value, "value"));
    }

    public static <T> OptionalField<T> ofNothing() {
        return new OptionalField<>(null);
    }

    public static <T> OptionalField<T> ofValueOrNullAsNothing(T value) {
        return new OptionalField<>(value);
    }

    public T getValueOrNothingAsNull() {
        return value;
    }

    public T getValue() {
        if (value == null) {
            throw new IllegalStateException("this OptionalField is absent");
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
