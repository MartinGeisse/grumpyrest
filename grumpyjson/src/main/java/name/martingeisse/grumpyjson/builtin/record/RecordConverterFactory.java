/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.record;

import name.martingeisse.grumpyjson.JsonRegistries;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * NOT PUBLIC API
 */
/*
Generates serializers and deserializers for record classes. This factory caches the generated converters and
re-uses a single converter for both serialization and deserialization, as well as for deserialization of all
parameterized variants. This reduces startup time because the meta-data, which must be obtained using reflection,
is built only once per record class.
 */
public final class RecordConverterFactory {

    private JsonRegistries registries;
    private final ConcurrentHashMap<Class<?>, RecordConverter<?>> map = new ConcurrentHashMap<>();

    /**
     * NOT PUBLIC API
     */
    public RecordConverterFactory() {
    }

    /**
     * NOT PUBLIC API
     *
     * @param registries ...
     */
    public void setRegistries(JsonRegistries registries) {
        this.registries = registries;
    }

    /**
     * NOT PUBLIC API
     *
     * @param clazz ...
     * @return ...
     * @param <T> ...
     */
    public <T> JsonSerializer<T> getSerializer(Class<T> clazz) {
        //noinspection unchecked
        return (JsonSerializer<T>) getConverter(clazz);
    }

    /**
     * NOT PUBLIC API
     *
     * @param rawClass ...
     * @return ...
     */
    public JsonDeserializer getDeserializer(Class<?> rawClass) {
        return getConverter(rawClass);
    }

    /**
     * NOT PUBLIC API
     *
     * @param clazz ...
     * @return ...
     */
    public RecordConverter<?> getConverter(Class<?> clazz) {
        return map.computeIfAbsent(clazz, ignored -> new RecordConverter<>(clazz, registries));
    }

}
