package name.martingeisse.grumpyjson;

import name.martingeisse.grumpyjson.builtin.record.RecordConverterFactory;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializerRegistry;
import name.martingeisse.grumpyjson.registry.NotRegisteredException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;
import name.martingeisse.grumpyjson.serialize.JsonSerializerRegistry;

import java.lang.reflect.Type;
import java.util.Objects;

public record JsonRegistries(
    JsonSerializerRegistry serializerRegistry,
    JsonDeserializerRegistry deserializerRegistry
) implements JsonProviders {

    public static JsonRegistries createDefault() {
        RecordConverterFactory recordConverterFactory = new RecordConverterFactory();
        JsonSerializerRegistry serializerRegistry = new JsonSerializerRegistry(recordConverterFactory);
        JsonDeserializerRegistry deserializerRegistry = new JsonDeserializerRegistry(recordConverterFactory);
        JsonRegistries registries = new JsonRegistries(serializerRegistry, deserializerRegistry);
        recordConverterFactory.setRegistries(registries);
        return registries;
    }

    /**
     * Registers the specified serializer.
     *
     * @param serializer the serializer to register
     */
    public void registerSerializer(name.martingeisse.grumpyjson.serialize.JsonSerializer<?> serializer) {
        Objects.requireNonNull(serializer, "serializer");
        serializerRegistry().register(serializer);
    }

    /**
     * Registers the specified deserializer.
     *
     * @param deserializer the deserializer to register
     */
    public void registerDeserializer(name.martingeisse.grumpyjson.deserialize.JsonDeserializer deserializer) {
        Objects.requireNonNull(deserializer, "deserializer");
        deserializerRegistry().register(deserializer);
    }

    /**
     * Registers the specified dual converter.
     *
     * @param converter the dual converter to register
     */
    public <T extends JsonSerializer<?> & JsonDeserializer> void registerDualConverter(T converter) {
        registerSerializer(converter);
        registerDeserializer(converter);
    }

    /**
     * Removes all registered converters from both registries. This is useful because the registries used by
     * grumpyjson contain default converters, and the code using it might not want to use them.
     */
    public void clear() {
        serializerRegistry.clear();
        deserializerRegistry.clear();
    }

    @Override
    public boolean supportsClassForSerialization(Class<?> clazz) {
        return serializerRegistry.supportsClassForSerialization(clazz);
    }

    @Override
    public <T> JsonSerializer<T> getSerializer(Class<T> clazz) throws NotRegisteredException {
        return serializerRegistry.getSerializer(clazz);
    }

    @Override
    public boolean supportsTypeForDeserialization(Type type) {
        return deserializerRegistry.supportsTypeForDeserialization(type);
    }

    @Override
    public JsonDeserializer getDeserializer(Type type) throws NotRegisteredException {
        return deserializerRegistry.getDeserializer(type);
    }

    /**
     * Seals the registries, moving from the configuration phase to the run-time phase.
     */
    public void seal() {
        serializerRegistry().seal();
        deserializerRegistry().seal();
    }

}
