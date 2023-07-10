/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.util.TypeUtil;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * NOT PUBLIC API
 */
public final class RecordInfo {

    private final Class<?> recordClass;
    private final List<ComponentInfo> componentInfos;
    private final Constructor<?> constructor;

    /**
     * NOT PUBLIC API
     *
     * @param recordClass ...
     */
    public RecordInfo(Class<?> recordClass) {
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
        this.componentInfos = List.of(componentInfos);
    }

    /**
     * ...
     *
     * @return ...
     */
    public Class<?> getRecordClass() {
        return recordClass;
    }

    /**
     * ...
     *
     * @return ...
     */
    public List<ComponentInfo> getComponentInfos() {
        return componentInfos;
    }

    /**
     * ...
     *
     * @param arguments ...
     * @return ...
     */
    public Object invokeConstructor(Object[] arguments) {
        try {
            return constructor.newInstance(arguments);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * NOT PUBLIC API
     */
    public class ComponentInfo {

        private final RecordComponent component;

        /**
         * ...
         *
         * @param component ...
         */
        public ComponentInfo(RecordComponent component) {
            this.component = component;
            component.getAccessor().setAccessible(true);
        }

        /**
         * ...
         *
         * @return ...
         */
        public String getName() {
            return component.getName();
        }

        /**
         * ...
         *
         * @return ...
         */
        public Type getType() {
            return component.getGenericType();
        }

        /**
         * ...
         *
         * @return ...
         */
        public Method getGetter() {
            return component.getAccessor();
        }

        /**
         * ...
         *
         * @param container ...
         * @return ...
         */
        public Object invokeGetter(Object container) {
            Method getter = getGetter();
            try {
                return getter.invoke(container);
            } catch (Exception e) {
                throw new JsonSerializationException("could not invoke getter " + getter + " on " + container);
            }
        }

        /**
         * ...
         *
         * @param concreteRecordType ...
         * @return ...
         */
        public Type getConcreteType(Type concreteRecordType) {
            if (concreteRecordType instanceof Class<?>) {
                return component.getGenericType();
            } else if (concreteRecordType instanceof ParameterizedType parameterizedRecordType) {
                TypeVariable<?>[] recordTypeParameters = recordClass.getTypeParameters();
                Type[] recordTypeArguments = parameterizedRecordType.getActualTypeArguments();
                if (recordTypeParameters.length != recordTypeArguments.length) {
                    throw new RuntimeException("type parameter/argument length mismatch for record " + recordClass);
                }
                Map<String, Type> typeArguments = new HashMap<>();
                for (int i = 0; i < recordTypeParameters.length; i++) {
                    typeArguments.put(recordTypeParameters[i].getName(), recordTypeArguments[i]);
                }
                return TypeUtil.replaceTypeVariables(component.getGenericType(), typeArguments);
            } else {
                throw new RuntimeException("cannot find concrete component type for record type " + concreteRecordType);
            }
        }

    }

}
