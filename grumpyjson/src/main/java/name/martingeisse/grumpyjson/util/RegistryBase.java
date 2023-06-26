/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.util;

import name.martingeisse.grumpyjson.AdapterProxy;
import name.martingeisse.grumpyjson.JsonTypeAdapter;
import name.martingeisse.grumpyjson.RecordAdapter;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for a registry which acts somewhat like a {@link Map}, but with the following differences:
 * <ul>
 *     <li>Each object to register ("value" in Map terminology) knows the keys it supports, i.e. the keys that can be
 *     used to obtain that object from the registry. Objects can be added without specifying a key, and an object
 *     added this way will be available using any of the keys it supports.</li>
 *     <li>Depending on the subclass, the registry may support auto-generation of an object for a key for which no
 *     object was registered manually.</li>
 *     <li>The only allowed ways to manipulate the registry are adding objects manually, auto-generation, and clearing
 *     the registry. In particular, removing individual objects is not supported.</li>
 *     <li>The only possible way to query the registry is to ask whether a key is supported, and to get the registered
 *     object for a key. In particular, iteration is not supported.</li>
 *     <li>The registry operated in two phases, configuration phase and run-time phase. In the configuration phase, only
 *     registering objects and clearing the registry are allowed. Querying the registry is not allowed. In the
 *     run-time phase, only querying is allowed, no manipulation. The transition from configuration phase to
 *     run-time phase is called "sealing" the registry. The separation into two phases allows internal optimization.
 *     </li>
 * </ul>
 * <p>
 * Note for subclasses: Auto-generation might be invoked multiple times in parallel for the same key from different
 * threads. One of the generated objects will end up in the registry, but another one may be returned and used
 * temporarily by the other thread.
 *
 * @param <K> the key type
 * @param <V> the type of registered object
 */
public abstract class RegistryBase<K, V extends RegistryBase.Value<K>> {

    private final List<V> manuallyAddedObjects = new ArrayList<>();
    private final AtomicBoolean sealed = new AtomicBoolean(false);
    private final ConcurrentMap<K, V> map = new ConcurrentHashMap<>();

    /**
     * A value that can be stored in a {@link RegistryBase} with key type K.
     *
     * @param <K> the key type
     */
    public interface Value<K> {

        /**
         * Checks whether this object supports the specified key,
         *
         * @param key the key to check
         * @return true if this object supports the key, false if not
         */
        boolean supports(K key);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // configuration-time methods
    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Throws an {@link IllegalStateException} if this registry has been sealed already.
     */
    protected final void ensureConfigurationPhase() {
        if (sealed.get()) {
            throw new IllegalStateException("this registry has been sealed already");
        }
    }

    /**
     * Removes all registered objects from this registry. This is useful because the registries used by grumpyjson
     * contain default objects, and the code using it might not want to use them.
     */
    public final void clear() {
        ensureConfigurationPhase();
        manuallyAddedObjects.clear();
    }

    /**
     * Registers an object with this registry.
     *
     * @param object the object to add
     */
    public final void register(V object) {
        Objects.requireNonNull(object, "object");
        ensureConfigurationPhase();
        manuallyAddedObjects.add(object);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // run-time methods
    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Throws an {@link IllegalStateException} if this registry has not yet been sealed.
     */
    protected final void ensureRunTimePhase() {
        if (!sealed.get()) {
            throw new IllegalStateException("this registry has not yet been sealed");
        }
    }

    /**
     * Checks whether the specified key is supported by any object that is registered with this registry or can be
     * auto-generated on the fly.
     * <p>
     * Note that there are some reasons why a registered object may claim to support a key, but fail at run-time when it
     * actually encounters that key. Such a key will still be "supported" by that object from the point-of-view of
     * the registry. Refer to the documentation of the individual registered objects for details.
     *
     * @param key the key to check
     * @return true if supported, false if not
     */
    public final boolean supports(K key) {
        Objects.requireNonNull(key, "key");
        ensureRunTimePhase();
        if (supportsAutoGeneration(key)) {
            return true;
        }
        if (map.containsKey(key)) {
            return true;
        }
        for (var object : manuallyAddedObjects) {
            if (object.supports(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a key is supported by this registry through auto-generation, that is, without ever registering
     * an explicit object for that key. If this method returns <code>true</code> for a key, so does
     * <code>supports</code>. Note that this method even returns true for a key for which an object could be
     * auto-generated in theory, but an object has already been added manually, so the auto-generation never actually
     * occurs.
     *
     * @param key the key to check
     * @return true if supported through auto-generation, false if not supported or only supported through an
     * explicitly registered object
     */
    public final boolean supportsAutoGeneration(K key) {
        Objects.requireNonNull(key, "key");
        ensureRunTimePhase();
        return supportsAutoGenerationInternal(key);
    }

    /**
     * Subclass-specific implementation for {@link #supportsAutoGeneration(Object)}. This method does not have to
     * check the key for null, nor whether this registry is in the configuration phase.
     *
     * @param key the key (never null)
     * @return true if and only if auto-generation for the specified key is supported
     */
    protected abstract boolean supportsAutoGenerationInternal(K key);

    /**
     * Returns a registered object for the specified key, auto-generating it if necessary and possible. This method will
     * throw an exception if no object was registered manually that supports the key and no object can be
     * auto-generated. If multiple objects have been registered that can handle the specified key, the one registered
     * earlier will take precedence.
     *
     * @param key the key to return an object for
     * @return the registered object, possibly auto-generated
     */
    public final V get(K key) {
        Objects.requireNonNull(key, "key");
        ensureRunTimePhase();

        // computeIfAbsent() cannot be used, if it behaves as it should, because recursively adding recognized keys
        // would cause a ConcurrentModificationException. Note that thread safety is not a concern here because,
        // while two threads might *both* decide to create a missing object, we just end up with either one of them
        // and they should be equivalent.
        V object = map.get(key);

        // check if one of the registered adapters supports this type
        if (object == null) {
            for (V objectFromList : manuallyAddedObjects) {
                if (objectFromList.supports(key)) {
                    object = objectFromList;
                    map.put(key, object);
                    break;
                }
            }
        }

        // check if we can auto-generate an adapter
        if (object == null && supportsAutoGeneration(key)) {

            // Next, install a proxy, so that recursive types don't crash the registry. Note that we don't put the
            // adapter/proxy into the adapterList because we already put it into the adapterMap, and it cannot handle
            // any types other than the exact type it gets generated for.
            var proxy = new AdapterProxy<>();
            map.put(type, proxy);

            // finally, create the actual adapter and set it as the proxy's target
            if (type instanceof Class<?> clazz) {
                object = new RecordAdapter<>(clazz, this);
            } else if (type instanceof ParameterizedType parameterizedType) {
                object = new RecordAdapter<>((Class<?>)parameterizedType.getRawType(), this);
            } else {
                throw new RuntimeException("internal error: erroneously selected a record adapter for type " + type);
            }

            //noinspection unchecked
            proxy.setTarget((JsonTypeAdapter<Object>) object);

        }

        // if this failed, then we don't have an appropriate adapter
        if (object == null) {
            throw new RuntimeException("no JSON type adapter found and can only auto-generate them for record types, found type: " + type);
        }

        return object;
    }

}
