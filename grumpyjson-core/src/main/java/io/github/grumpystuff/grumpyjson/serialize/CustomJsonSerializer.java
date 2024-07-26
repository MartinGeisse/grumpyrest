package io.github.grumpystuff.grumpyjson.serialize;

import io.github.grumpystuff.grumpyjson.json_model.JsonElement;

/**
 * Helper methods to build custom serializers for common cases.
 */
public final class CustomJsonSerializer {

    // prevent instantiation
    private CustomJsonSerializer() {
    }

    /**
     * Builds a serializer for a single class type from a code body.
     *
     * @param clazz the type to serialize
     * @param body the code body
     * @return the serializer
     * @param <T> the type to serialize
     */
    public static <T> JsonSerializer<T> from(Class<T> clazz, SerializerBody<T> body) {
        return new JsonSerializer<>() {

            @Override
            public boolean supportsClassForSerialization(Class<?> clazz2) {
                return clazz2.equals(clazz);
            }

            @Override
            public JsonElement serialize(T value) throws JsonSerializationException {
                return body.serialize(value);
            }

        };
    }

    /**
     * The body of a custom serializer
     *
     * @param <T> the type to serialize
     */
    public interface SerializerBody<T> {

        /**
         * Serializes a value.
         *
         * @param value the value to serialize
         * @return the output JSON
         * @throws JsonSerializationException if the value cannot be serialized
         */
        JsonElement serialize(T value) throws JsonSerializationException;

    }

}
