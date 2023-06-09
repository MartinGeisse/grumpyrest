package name.martingeisse.grumpyjson;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.Objects;

class RecordInfo<T> {

    private final Class<T> record;
    private final ImmutableList<ComponentInfo> componentInfos;
    private final Constructor<?> constructor;

    RecordInfo(Class<T> record) {
        Objects.requireNonNull(record, "record");
        if (!record.isRecord()) {
            throw new IllegalArgumentException("not a record: " + record);
        }
        this.record = record;

        RecordComponent[] components = record.getRecordComponents();
        Class<?>[] rawComponentTypes = new Class<?>[components.length];
        ComponentInfo[] componentInfos = new ComponentInfo[components.length];
        for (int i = 0; i < components.length; i++) {
            RecordComponent component = components[i];
            rawComponentTypes[i] = component.getType();
            componentInfos[i] = new ComponentInfo(component);
        }
        try {
            this.constructor = record.getDeclaredConstructor(rawComponentTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("could not find canonical constructor for record type " + record);
        }
        this.componentInfos = ImmutableList.copyOf(componentInfos);
    }

    public final Class<T> getRecord() {
        return record;
    }

    public final ImmutableList<ComponentInfo> getComponentInfos() {
        return componentInfos;
    }

    public final Constructor<?> getConstructor() {
        return constructor;
    }

    record ComponentInfo(RecordComponent component) {

        public String getName() {
            return component.getName();
        }

        public Type getType() {
            return component.getGenericType();
        }

        public Method getGetter() {
            return component.getAccessor();
        }

        public Object invokeGetter(Object container) {
            Method getter = getGetter();
            try {
                return getter.invoke(container);
            } catch (Exception e) {
                throw new JsonGenerationException("could not invoke getter " + getter + " on " + container);
            }
        }

    }

}
