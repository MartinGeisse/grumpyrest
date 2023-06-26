/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.util;

import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyjson.JsonTypeAdapter;

import java.lang.reflect.Type;

/**
 * Base class for registries that use {@link Type}-typed keys. This class adds some convenience methods.
 *
 * @param <V> the type of registered object
 */
public abstract class TypeKeyedRegistryBase<V extends RegistryBase.Value<Type>> extends RegistryBase<Type, V> {

    /**
     * This is a convenience wrapper for {@link #getTypeAdapter(Type)} in case the type is a raw class object. It
     * returns the adapter as a typed adapter.
     *
     * @param clazz the class to return an adapter for
     * @return the type adapter
     * @param <T> the adapted type as a static type
     */
    public <T> JsonTypeAdapter<T> get(Class<T> clazz) {
        //noinspection unchecked
        return (JsonTypeAdapter<T>)getTypeAdapter((Type)clazz);
    }

    /**
     * This is a convenience wrapper for {@link #getTypeAdapter(Type)} in case the type is a not raw class object but
     * can be specified statically using a type token. It returns the adapter as a typed adapter.
     *
     * @param typeToken a type token for the type to return an adapter for
     * @return the type adapter
     * @param <T> the adapted type as a static type
     */
    public <T> JsonTypeAdapter<T> get(TypeToken<T> typeToken) {
        //noinspection unchecked
        return (JsonTypeAdapter<T>)getTypeAdapter(typeToken.getType());
    }


}
