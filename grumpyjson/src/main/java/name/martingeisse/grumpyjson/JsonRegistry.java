/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class JsonRegistry {

    // This list is not thread-safe, but adding type adapters after starting to serve requests would mess up
    // things anyway.
    private final List<JsonTypeAdapter<?>> adapterList = new ArrayList<>();
    private final ConcurrentMap<Type, JsonTypeAdapter<?>> adapterMap = new ConcurrentHashMap<>();

    /**
     * Constructor
     */
    public JsonRegistry() {
        // needed to silence Javadoc error because the implicit constructor doesn't have a doc comment
    }

    // ----------------------------------------------------------------------------------------------------------------
    // configuration-time methods
    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Removes all type adapters from this registry. This is useful because the registry that is used by a newly
     * created {@link JsonEngine} contains default adapters, and the code using it might not want to use them.
     */
    public void clearTypeAdapters() {
        adapterList.clear();
    }

    /**
     * Adds a type adapter to this registry, to be used when parsing and generating code with the {@link JsonEngine}
     * that uses this registry.
     *
     * @param adapter the adapter to add
     */
    public void addTypeAdapter(JsonTypeAdapter<?> adapter) {
        adapterList.add(Objects.requireNonNull(adapter, "adapter"));
    }

    // ----------------------------------------------------------------------------------------------------------------
    // run-time methods
    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Checks whether the specified type is supported by any adapter in this registry.
     * <p>
     * Note that there are some reasons why an adapter may claim to support a type, but fail at run-time when it
     * actually encounters that type. Such a type will still be "supported" by that adapter from the point-of-view of
     * the registry. Refer to the documentation of the individual type adapters for details.
     *
     * @param type the type to check
     * @return true if supported, false if not
     */
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        if (supportsAdapterAutoGeneration(type)) {
            return true;
        }
        if (adapterMap.containsKey(type)) {
            return true;
        }
        for (var adapter : adapterList) {
            if (adapter.supportsType(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a type is supported by this registry through auto-generation, that is, without ever registering
     * an explicit adapter for that type. If this method returns <tt>true</tt> for a type, so does <tt>supportsType</tt>.
     *
     * @param type the type to check
     * @return true if supported through auto-generation, false if not supported or only supported through an
     * explicitly added type adapter
     */
    public boolean supportsAdapterAutoGeneration(Type type) {
        Objects.requireNonNull(type, "type");
        if (type instanceof Class<?> c) {
            return c.isRecord();
        } else if (type instanceof ParameterizedType p && p.getRawType() instanceof Class<?> raw) {
            return raw.isRecord();
        } else {
            return false;
        }
    }

    /**
     * Returns a type adapter for the specified type, auto-generating it if necessary and possible. This method will
     * throw an exception if no adapter for that type was registered and no adapter can be auto-generated.
     *
     * @param type the type to return an adapter for
     * @return the type adapter
     */
    public JsonTypeAdapter<?> getTypeAdapter(Type type) {
        Objects.requireNonNull(type, "type");

        // computeIfAbsent() cannot be used, if it behaves as it should, because recursively adding recognized types
        // would cause a ConcurrentModificationException. Note that thread safety is not a concern here because,
        // while two threads might *both* decide to create a missing adapter, we just end up with either one of them
        // and they should be equivalent.
        JsonTypeAdapter<?> adapter = adapterMap.get(type);

        // check if one of the registered adapters supports this type
        if (adapter == null) {
            for (JsonTypeAdapter<?> adapterFromList : adapterList) {
                if (adapterFromList.supportsType(type)) {
                    adapter = adapterFromList;
                    adapterMap.put(type, adapter);
                    break;
                }
            }
        }

        // check if we can auto-generate an adapter
        if (adapter == null && supportsAdapterAutoGeneration(type)) {

            // Next, install a proxy, so that recursive types don't crash the registry. Note that we don't put the
            // adapter/proxy into the adapterList because we already put it into the adapterMap, and it cannot handle
            // any types other than the exact type it gets generated for.
            var proxy = new AdapterProxy<>();
            adapterMap.put(type, proxy);

            // finally, create the actual adapter and set it as the proxy's target
            if (type instanceof Class<?> clazz) {
                adapter = new RecordAdapter<>(clazz, this);
            } else if (type instanceof ParameterizedType parameterizedType) {
                adapter = new RecordAdapter<>((Class<?>)parameterizedType.getRawType(), this);
            } else {
                throw new RuntimeException("internal error: erroneously selected a record adapter for type " + type);
            }

            //noinspection unchecked
            proxy.setTarget((JsonTypeAdapter<Object>) adapter);

        }

        // if this failed, then we don't have an appropriate adapter
        if (adapter == null) {
            throw new RuntimeException("no JSON type adapter found and can only auto-generate them for record types, found type: " + type);
        }

        return adapter;
    }

}
