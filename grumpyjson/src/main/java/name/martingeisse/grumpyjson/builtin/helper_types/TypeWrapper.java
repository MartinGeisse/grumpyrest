/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

// no javadoc because this may disappear in a future version, if we go the route to only use the run-time class during
// serialization.
public abstract class TypeWrapper<T> {

    private final T value;
    private final Type type;

    /**
     * Wraps the specified value. The type is derived from the type argument.
     *
     * @param value the value to wrap
     */
    protected TypeWrapper(T value) {
        this.value = value;
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            this.type = parameterizedType.getActualTypeArguments()[0];
        } else if (genericSuperclass == TypeWrapper.class) {
            throw new RuntimeException("used TypeWrapper without a type argument");
        } else {
            throw new RuntimeException("found indirect subclass of TypeWrapper");
        }
    }

    public final T getValue() {
        return value;
    }

    public final Type getType() {
        return type;
    }

}
