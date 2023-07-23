/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.serialize;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.builtin.helper_types.OptionalField;

import java.util.Optional;

/**
 * Defines the conversion of Java objects to JSON for one or more classes.
 * <p>
 * The JSON side is represented by {@link JsonElement}. That is, generating the JSON syntax is out-of-scope
 * for this interface. Only mapping the higher-level structure is done here.
 * <p>
 * Serialization is always based on the run-time class of the values to serialize. While static type information is
 * usually available, this is not always the case (e.g. for top-level values) and we want to avoid a mix of both
 * approaches to reduce complexity.
 * <p>
 * There is currently no precedence rule for serializers based on specificity: If one serializer supports superclass A,
 * and a second one supports subclass B extends A, then for an instance of B, the second one does NOT take precedence
 * just because it is more specific. Instead, the registry will select the serializer that was registered later.
 *
 * @param <T> the type to convert
 */
public interface JsonSerializer<T> {

    /**
     * Checks if this serializer supports the specified class.
     *
     * @param clazz the class to check
     * @return true if supported, false if not
     */
    boolean supportsClassForSerialization(Class<?> clazz);

    /**
     * Converts a value to JSON.
     * <p>
     * This method must not be called with values for whose class {@link #supportsClassForSerialization(Class)} returns
     * false. Calling it with such values anyway results in undefined behavior.
     * <p>
     * This method is not supported for values that can vanish during serialization, such as {@link OptionalField} --
     * {@link #serializeOptional(Object)} should be called instead. If this method is called with such values anyway,
     * it should always fail, even when the value does not vanish, to capture bugs early. Examples where this happens:
     * - in a list of OptionalFields. While we could simply remove vanishing elements, doing so is just a weird way
     *   of filtering the list before serialization, which can be done the usual way. Moreover, there is no
     *   useful interpretation of a list-of-OptionalField during deserialization since all elements found in the JSON
     *   are known to be present, and vanishing elements cannot be found.
     * - when turning a top-level OptionalField to JSON
     * - when nesting two OptionalFields
     *
     * @param value the value to convert to JSON
     * @return the generated JSON
     * @throws JsonSerializationException if the value is in an inconsistent state, or in a state that cannot be
     * converted to JSON
     */
    JsonElement serialize(T value) throws JsonSerializationException;

    /**
     * Converts a value to JSON in a context in which a non-existing JSON value can be handled, and therefore
     * supports values that turn out to vanish during serialization, such as {@link OptionalField}. The primary use
     * case is for optional object properties.
     * <p>
     * Most types cannot vanish, and therefore have no special behavior in a context that supports vanishable values,
     * so the standard implementation just delegates to {@link #serialize(Object)}.
     * <p>
     * This method must not be called with values for whose class {@link #supportsClassForSerialization(Class)} returns
     * false. Calling it with such values anyway results in undefined behavior.
     *
     * @param value the value to convert to JSON
     * @return the generated JSON, or nothing in case the value vanishes in JSON
     * @throws JsonSerializationException if the value is in an inconsistent state, or in a state that cannot be
     * converted to JSON
     */
    default Optional<JsonElement> serializeOptional(T value) throws JsonSerializationException {
        return Optional.of(serialize(value));
    }

}
