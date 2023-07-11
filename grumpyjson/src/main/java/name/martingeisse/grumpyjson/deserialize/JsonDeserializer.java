/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.deserialize;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.ExceptionMessages;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Defines the conversion from JSON to a Java object / value for one or more types.
 * <p>
 * The JSON side is represented by {@link JsonElement}. That is, parsing the JSON syntax is out-of-scope
 * for this interface. Only mapping the higher-level structure is done here.
 * <p>
 * Deserializing JSON is driven by the {@link Type} to deserialize. This is crucial because there is otherwise no
 * information about the Java class to generate from it. The class alone might not be enough though. This is most
 * obvious when deserializing an array to a {@link List}: Even when the caller specifies to deserialize the List class,
 * the deserializer will fail for the list elements because the element type is unknown. Therefore the deserializer
 * gets passed the whole type, such as List&lt;Integer&gt;, so it knows to deserialize the elements as {@link Integer}.
 * <p>
 * There is currently no precedence rule for deserializers based on specificity: If one deserializer supports supertype
 * A, and a second one supports subtype B of A, then for an instance of B, the second one does NOT take precedence
 * just because it is more specific. Instead, the registry will select the deserializer that was registered earlier.
 */
public interface JsonDeserializer {

    /**
     * Checks if this deserializer supports the specified type.
     *
     * @param type the type to check
     * @return true if supported, false if not
     */
    boolean supportsTypeForDeserialization(Type type);

    /**
     * Converts a value from JSON.
     * <p>
     * This method must not be called with a type for which {@link #supportsTypeForDeserialization(Type)} returns
     * false. Calling it with such types anyway results in undefined behavior.
     *
     * @param json the JSON
     * @param type the type to deserialize
     * @return the deserialized value
     * @throws JsonDeserializationException if the JSON does not match the expected structure
     */
    Object deserialize(JsonElement json, Type type) throws JsonDeserializationException;

    /**
     * Converts a value from an absent JSON fragment. This can be used to return a default for optional object
     * properties.
     * <p>
     * The standard implementation of this method is that missing values are not tolerated, and throws an exception.
     * <p>
     * This method must not be called with a type for which {@link #supportsTypeForDeserialization(Type)} returns
     * false. Calling it with such types anyway results in undefined behavior.
     *
     * @param type the type to deserialize
     * @return the default value for that type
     * @throws JsonDeserializationException if the JSON does not match the expected structure
     */
    default Object deserializeAbsent(Type type) throws JsonDeserializationException {
        throw new JsonDeserializationException(ExceptionMessages.MISSING_PROPERTY);
    }

}
