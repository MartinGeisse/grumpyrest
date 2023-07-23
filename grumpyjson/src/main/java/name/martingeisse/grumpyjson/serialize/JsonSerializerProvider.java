package name.martingeisse.grumpyjson.serialize;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializerRegistry;
import name.martingeisse.grumpyjson.registry.NotRegisteredException;

import java.util.Objects;
import java.util.Optional;

/**
 * Abstracts the run-time methods from the {@link JsonSerializerRegistry} so that serializers which have to
 * look up dependencies don't depend on the full registry.
 */
public interface JsonSerializerProvider {

    /**
     * Checks whether the specified class is supported for serialization by any serializer that is known to this
     * provider. Since this provider usually abstracts a {@link JsonSerializerRegistry}, this includes serializers
     * that can be auto-generated on the fly.
     * <p>
     * Note that there are some reasons why a registered serializer may claim to support a class, but fail at run-time
     * when it actually encounters that class. Such a class will still be "supported" by that serializer from the
     * point-of-view of this provider. Refer to the documentation of the individual serializers for details.
     *
     * @param clazz the class to check
     * @return true if supported, false if not
     */
    boolean supportsClassForSerialization(Class<?> clazz);

    /**
     * Returns a registered serializer for the specified class, auto-generating it if necessary and possible. This
     * method will throw an exception if no serializer was registered manually that supports the class and no
     * serializer can be auto-generated. If multiple serializers have been registered that can handle the
     * specified class, the one registered later will take precedence.
     *
     * @param clazz the class to return a serializer for
     * @return the registered serializer, possibly auto-generated
     * @throws NotRegisteredException if the class is not known to this provider
     */
    <T> JsonSerializer<T> getSerializer(Class<T> clazz) throws NotRegisteredException;

    /**
     * Turns a value into a {@link JsonElement}.
     *
     * @param value the value to convert
     * @return the JSON element
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned
     * into JSON, or is an instance of a class for which {@link #supportsClassForSerialization(Class)} returns false,
     * or is a value that requires the usage of {@link #serializeOptional(Object)}.
     */
    default JsonElement serialize(Object value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        Class<?> clazz = value.getClass();
        @SuppressWarnings("rawtypes") JsonSerializer serializer;
        try {
            serializer = getSerializer(clazz);
        } catch (NotRegisteredException e) {
            throw new JsonSerializationException("no serializer for type: " + clazz);
        }
        //noinspection unchecked
        return serializer.serialize(value);
    }

    /**
     * Turns a value into an optional {@link JsonElement}. This method is meant to be called in a context in which
     * values can vanish, such as object properties.
     *
     * @param value the value to convert
     * @return the JSON element or nothing
     * @throws JsonSerializationException if the value is in an inconsistent state or a state that cannot be turned
     * into JSON, or is an instance of a class for which {@link #supportsClassForSerialization(Class)} returns false.
     */
    default Optional<JsonElement> serializeOptional(Object value) throws JsonSerializationException {
        Objects.requireNonNull(value, "value");
        Class<?> clazz = value.getClass();
        @SuppressWarnings("rawtypes") JsonSerializer serializer;
        try {
            serializer = getSerializer(clazz);
        } catch (NotRegisteredException e) {
            throw new JsonSerializationException("no serializer for type: " + clazz);
        }
        //noinspection unchecked
        return serializer.serializeOptional(value);
    }

}
