package name.martingeisse.grumpyjson;

import com.google.gson.reflect.TypeToken;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class JsonRegistry {

    private final ConcurrentMap<TypeToken<?>, JsonTypeAdapter<?>> typeAdapters = new ConcurrentHashMap<>();

    public <T> void addTypeAdapter(TypeToken<T> type, JsonTypeAdapter<T> adapter) {
        if (typeAdapters.put(type, adapter) != null) {
            throw new RuntimeException("two type adapters have been registered for the same type: " + type);
        }
    }

    public <T> JsonTypeAdapter<T> getTypeAdapter(TypeToken<T> type) {
        // computeIfAbsent() cannot be used, if it behaves as it should, because recursively adding recognized types
        // would cause a ConcurrentModificationException. Note that thread safety is not a concern here because,
        // while two threads might *both* decide to create a missing adapter, we just end up with either one of them
        // and they should be equivalent.
        JsonTypeAdapter<?> adapter = typeAdapters.get(type);
        if (adapter == null) {

            // first, check if we can auto-generate an adapter for this type at all
            var rawType = type.getRawType();
            if (!rawType.isRecord() || !TypeToken.get(rawType).equals(type)) {
                throw new RuntimeException("can only auto-generate JSON type adapters for non-generic record types, found " + rawType);
            }

            // next, install a proxy, so that recursive types don't crash the registry
            var proxy = new AdapterProxy<T>();
            typeAdapters.put(type, proxy);

            // finally, create the actual adapter and set it as the proxy's target
            adapter = new RecordAdapter<>(rawType, this);
            //noinspection unchecked
            proxy.setTarget((JsonTypeAdapter<T>)adapter);

        }
        //noinspection unchecked
        return (JsonTypeAdapter<T>)adapter;
    }

}
