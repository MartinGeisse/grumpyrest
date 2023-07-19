/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.record;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.List;

public final class RecordInfo {

    private final Class<?> recordClass;
    private final List<ComponentInfo> componentInfos;
    private final Constructor<?> constructor;

    public RecordInfo(Class<?> recordClass) {}

    public Class<?> getRecordClass() {}
    public List<ComponentInfo> getComponentInfos() {}
    public Object invokeConstructor(Object[] arguments) {}

    public class ComponentInfo {

        private final RecordComponent component;

        public String getName() {}
        public Type getType() {}
        public Method getGetter() {}

        public Object invokeGetter(Object container) {}

        public Type getConcreteType(Type concreteRecordType) {}

    }

}
