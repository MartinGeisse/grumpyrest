package name.martingeisse.grumpyjson;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.Objects;

final class RecordInfo<T> {

    private final Class<T> record;
    private final ImmutableList<ComponentInfo> componentInfos;
    private final Constructor<T> constructor;

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
            constructor = record.getDeclaredConstructor(rawComponentTypes);
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("could not find canonical constructor for record type " + record);
        }
        this.componentInfos = ImmutableList.copyOf(componentInfos);
    }

    public Class<T> getRecord() {
        return record;
    }

    public ImmutableList<ComponentInfo> getComponentInfos() {
        return componentInfos;
    }

    public T invokeConstructor(Object[] arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
