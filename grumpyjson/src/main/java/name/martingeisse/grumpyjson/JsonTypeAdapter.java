/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.builtin.helper_types.OptionalField;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

/**
 * Defines the conversion from and to JSON for a single type.
 * <p>
 * The JSON side is represented by {@link JsonElement}. That is, parsing and generating the JSON syntax is out-of-scope
 * for this interface. Only mapping the higher-level structure is done here.
 * <p>
 * This interface gets the {@link Type} to convert for both parsing and generating JSON. For parsing, this is crucial
 * because there is otherwise no information about the Java class to generate from it. For generating JSON, the class
 * of the value to convert is available. However, in both cases, the Java class might not be enough because it may be
 * generic. This is most obvious when parsing an array to a {@link List}: Even when the caller specifies to parse
 * the List class, the parser will fail for the list elements because the element type is unknown. Therefore the
 * type adapter gets the whole type, such as List&lt;Integer&gt;, so it knows to parse the elements as {@link Integer}.
 *
 * @param <T> the type to convert
 */
public interface JsonTypeAdapter<T> {

    /**
     * Checks if this adapter supports the specified type.
     *
     * @param type the type to check
     * @return true if this adapter supports the specified type, false if not
     */
    boolean supportsType(Type type);

    /**
     * Converts a value from JSON.
     *
     * @param json the JSON
     * @param type the type to parse
     * @return the parsed value
     * @throws JsonDeserializationException if the JSON does not match the expected structure
     */
    T deserialize(JsonElement json, Type type) throws JsonDeserializationException;

    /**
     * Converts a value from an absent JSON fragment. This can be used to return a default for optional object
     * properties.
     * <p>
     * The standard implementation of this method is that missing values are not tolerated, and throws a validation
     * exception.
     *
     * @param type the type to parse
     * @return the parsed value
     * @throws JsonDeserializationException if the JSON does not match the expected structure
     */
    default T deserializeAbsent(Type type) throws JsonDeserializationException {
        throw new JsonDeserializationException(ExceptionMessages.MISSING_PROPERTY);
    }

    /**
     * Converts a value to JSON.
     * <p>
     * This method is not supported for types that can vanish during serialization, such as {@link OptionalField}.
     * For such types, it is suggested that this method always fails, even when the value does not vanish, to
     * capture bugs early. Examples where this happens:
     * - in a list of OptionalFields. While we could simply remove vanishing elements, doing so is just a weird way
     *   of filtering the list before serialization, which can be done the usual way. Moreover, there is no
     *   useful interpretation of a list-of-OptionalField in fromJson() since all elements found in the JSON are
     *   known to be present, and vanishing elements cannot be found.
     * - when turning a top-level OptionalField to JSON
     * - when nesting two OptionalFields
     *
     * @param value the value to convert to JSON
     * @param type the type to convert
     * @return the generated JSON
     * @throws JsonSerializationException if the value is in an inconsistent state, or in a state that cannot be converted to JSON
     */
    JsonElement serialize(T value, Type type) throws JsonSerializationException;

    /**
     * Converts a value to JSON in a context in which a non-existing JSON value can be handled, and therefore
     * supports values that turn out to vanish during serialization, such as {@link OptionalField}. The primary use
     * case is for optional object properties.
     * <p>
     * Most types cannot vanish, and therefore have no special behavior in a context that supports vanishable values,
     * so the standard implementation just delegates to {@link #serialize(Object, Type)}.
     *
     * @param value the value to convert to JSON
     * @param type the type to convert
     * @return the generated JSON, or nothing in case the value vanishes in JSON
     * @throws JsonSerializationException if the value is in an inconsistent state, or in a state that cannot be converted to JSON
     */
    default Optional<JsonElement> serializeOptional(T value, Type type) throws JsonSerializationException {
        return Optional.of(serialize(value, type));
    }

}
