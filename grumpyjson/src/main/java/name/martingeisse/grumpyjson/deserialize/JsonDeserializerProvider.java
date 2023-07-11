package name.martingeisse.grumpyjson.deserialize;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.registry.NotRegisteredException;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Abstracts the run-time methods from the {@link JsonDeserializerRegistry} so that deserializers which have to
 * look up dependencies don't depend on the full registry.
 */
public interface JsonDeserializerProvider {

    /**
     * Checks whether the specified type is supported for deserialization by any deserializer that is known to this
     * provider. Since this provider usually abstracts a {@link JsonDeserializerRegistry}, this includes deserializers
     * that can be auto-generated on the fly.
     * <p>
     * Note that there are some reasons why a registered deserializer may claim to support a type, but fail at run-time
     * when it actually encounters that type. Such a type will still be "supported" by that deserializer from the
     * point-of-view of this provider. Refer to the documentation of the individual deserializers for details.
     *
     * @param type the type to check
     * @return true if supported, false if not
     */
    boolean supportsTypeForDeserialization(Type type);

    /**
     * Returns a registered deserializer for the specified type, auto-generating it if necessary and possible. This
     * method will throw an exception if no deserializer was registered manually that supports the type and no
     * deserializer can be auto-generated. If multiple deserializers have been registered that can handle the
     * specified type, the one registered earlier will take precedence.
     *
     * @param type the type to return a deserializer for
     * @return the registered deserializer, possibly auto-generated
     * @throws NotRegisteredException if the type is not known to this provider
     */
    JsonDeserializer getDeserializer(Type type) throws NotRegisteredException;

    /**
     * Convenience method to find a deserializer for the specified {@link Type}, then use it to deserialize the
     * {@link JsonElement}.
     *
     * @param source the source element
     * @param type the target type to deserialize to
     * @return the deserialized value
     * @throws JsonDeserializationException if the JSON does not match the target type, or if the target type is a
     * type for which {@link #supportsTypeForDeserialization(Type)} returns false
     */
    default Object deserialize(JsonElement source, Type type) throws JsonDeserializationException {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        JsonDeserializer deserializer;
        try {
            deserializer = getDeserializer(type);
        } catch (NotRegisteredException e) {
            throw new JsonDeserializationException("no deserializer registered for type: " + type);
        }
        return deserializer.deserialize(source, type);
    }

    /**
     * Convenience method to find a deserializer for the specified {@link Type}, then use it to generate a default
     * value.
     *
     * @param type the target type to deserialize to
     * @return the generated default value
     * @throws JsonDeserializationException if no default value can be generated for the specified type, or if the
     * target type is a type for which {@link #supportsTypeForDeserialization(Type)} returns false
     */
    default Object deserializeAbsent(Type type) throws JsonDeserializationException {
        Objects.requireNonNull(type, "type");
        JsonDeserializer deserializer;
        try {
            deserializer = getDeserializer(type);
        } catch (NotRegisteredException e) {
            throw new JsonDeserializationException("no deserializer registered for type: " + type);
        }
        return deserializer.deserializeAbsent(type);
    }

}
