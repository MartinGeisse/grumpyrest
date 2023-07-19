/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * NOT PUBLIC API
 */
public class TypeUtil {

    /**
     * TODO document why this is a bug in the deserializer
     */
    public static Type[] expectParameterizedType(Type type, Class<?> expectedRawClass, int expectedNumberOfTypeArguments) {
    }

    /**
     * TODO document why this is a bug in the deserializer
     */
    public static Type expectSingleParameterizedType(Type type, Class<?> expectedRawClass) {
    }

    /**
     * ....
     * version of that type like <code>MyType&lt;String,List&lt;A&gt;&gt;</code> -- that is, the binding for type
     * parameter B cannot use the type variable A to define its bound type.
     * TODO this is somewhat confusing to explain because the binding *can* use a type variable which is bound in the
     * scope it occurs, and that type variable can also have the name <code>A</code>, but it's a different <code>A</code>
     * from the unbound variable in the type passed here. Also, such "outer" type variables are expected to be replaced
     * before calling this method, so they should not occur anyway.
     */
    public static Type replaceTypeVariables(Type original, Map<String, Type> bindings) {
        // ...
    }

}
