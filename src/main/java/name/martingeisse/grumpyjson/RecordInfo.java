/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

final class RecordInfo {

    private final Class<?> recordClass;
    private final ImmutableList<ComponentInfo> componentInfos;
    private final Constructor<?> constructor;

    RecordInfo(Class<?> recordClass) {
        Objects.requireNonNull(recordClass, "record");
        if (!recordClass.isRecord()) {
            throw new IllegalArgumentException("not a record: " + recordClass);
        }
        this.recordClass = recordClass;

        RecordComponent[] components = recordClass.getRecordComponents();
        Class<?>[] rawComponentTypes = new Class<?>[components.length];
        ComponentInfo[] componentInfos = new ComponentInfo[components.length];
        for (int i = 0; i < components.length; i++) {
            RecordComponent component = components[i];
            rawComponentTypes[i] = component.getType();
            componentInfos[i] = new ComponentInfo(component);
        }
        try {
            constructor = recordClass.getDeclaredConstructor(rawComponentTypes);
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("could not find canonical constructor for record type " + recordClass);
        }
        this.componentInfos = ImmutableList.copyOf(componentInfos);
    }

    public Class<?> getRecordClass() {
        return recordClass;
    }

    public ImmutableList<ComponentInfo> getComponentInfos() {
        return componentInfos;
    }

    public Object invokeConstructor(Object[] arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public class ComponentInfo {

        private final RecordComponent component;

        public ComponentInfo(RecordComponent component) {
            this.component = component;
        }

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

        public Type getConcreteType(Type concreteRecordType) {
            if (concreteRecordType instanceof Class<?>) {
                return component.getGenericType();
            } else if (concreteRecordType instanceof ParameterizedType parameterizedRecordType) {
                TypeVariable<?>[] recordTypeParameters = recordClass.getTypeParameters();
                Type[] recordTypeArguments = parameterizedRecordType.getActualTypeArguments();
                if (recordTypeParameters.length != recordTypeArguments.length) {
                    throw new RuntimeException("type parameter/argument length mismatch for record " + recordClass);
                }
                Map<TypeVariable<?>, Type> typeArgumentMap = new HashMap<>();
                for (int i = 0; i < recordTypeParameters.length; i++) {
                    typeArgumentMap.put(recordTypeParameters[i], recordTypeArguments[i]);
                }
                return TypeUtils.unrollVariables(typeArgumentMap, component.getGenericType());
            } else {
                throw new RuntimeException("cannot find concrete component type for record type " + concreteRecordType);
            }
        }

    }

}
