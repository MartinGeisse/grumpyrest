package name.martingeisse.grumpyjson;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.builtin.json.JsonOptional;

import java.lang.reflect.Type;
import java.util.Optional;

public interface JsonTypeAdapter<T> {

    /**
     * Checks if this adapter supports the specified type.
     */
    boolean supportsType(Type type);

    /**
     * Converts a value from JSON.
     */
    T fromJson(JsonElement json, Type type) throws JsonValidationException;

    /**
     * Converts a value from an absent JSON fragment. This can be used to return a default for optional object
     * properties.
     *
     * The standard implementation of this method is that missing values are not tolerated, and throws a validation
     * exception.
     */
    default T fromAbsentJson(Type type) throws JsonValidationException {
        throw new JsonValidationException(ExceptionMessages.MISSING_PROPERTY);
    }

    /**
     * Converts a value to JSON.
     *
     * This method is not supported for types that can vanish during serialization, such as {@link JsonOptional}.
     * For such types, it is suggested that this method always fails, even when the value does not vanish, to
     * capture bugs early. Examples where this happens:
     * - in a list of JsonOptionals. While we could simply remove vanishing elements, doing so is just a weird way
     *   of filtering the list before serialization, which can be done the usual way. Moreover, there is no
     *   useful interpretation of a list-of-JsonOptional in fromJson() since all elements found in the JSON are
     *   known to be present, and vanishing elements cannot be found.
     * - when turning a top-level JsonOptional to JSON
     * - when nesting two JsonOptionals
     */
    JsonElement toJson(T value, Type type) throws JsonGenerationException;

    /**
     * Converts a value to JSON in a context in which a non-existing JSON value can be handled, and therefore
     * supports values that turn out to vanish during serialization, such as {@link JsonOptional}. The primary use
     * case is for optional object properties.
     *
     * Most types cannot vanish, so the standard implementation just delegates to toJson().
     */
    default Optional<JsonElement> toOptionalJson(T value, Type type) throws JsonGenerationException {
        return Optional.of(toJson(value, type));
    }

}
