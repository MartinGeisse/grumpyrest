package io.github.grumpystuff.grumpyjson.deserialize;

import io.github.grumpystuff.grumpyjson.json_model.JsonElement;

import java.lang.reflect.Type;

/**
 * Helper methods to build custom deserializers for common cases.
 */
public final class CustomJsonDeserializer {

    // prevent instantiation
    private CustomJsonDeserializer() {
    }

    /**
     * Builds a deserializer for a single class type from a code body.
     *
     * @param clazz the type to deserialize
     * @param body the code body
     * @return the deserializer
     * @param <T> the type to deserialize
     */
    public static <T> JsonDeserializer from(Class<T> clazz, DeserializerBody<T> body) {
        return new JsonDeserializer() {

            @Override
            public boolean supportsTypeForDeserialization(Type type) {
                return type.equals(clazz);
            }

            @Override
            public Object deserialize(JsonElement json, Type type) throws JsonDeserializationException {
                return body.deserialize(json);
            }

        };
    }

    /**
     * The body of a custom deserializer
     *
     * @param <T> the type to deserialize
     */
    public interface DeserializerBody<T> {

        /**
         * Deserializes a value.
         *
         * @param json the input JSON
         * @return the deserialized value
         * @throws JsonDeserializationException if the value cannot be deserialized
         */
        T deserialize(JsonElement json) throws JsonDeserializationException;
    }

}
