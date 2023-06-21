/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * NOT PUBLIC API
 */
public class TypeUtil {

    /**
     * A common thing to check in type adapters is whether a requested type is a parameterized type with a specific
     * raw type and a specific number of type parameters, but without further restricting the type arguments used
     * for these parameters -- instead, obtaining the type arguments to do something with them. This method implements
     * this pattern.
     * <p>
     * For example, the List&lt;...&gt; type adapter wants to know if the requested type is
     * List&lt;...&gt;, that is, is a parameterized type with the raw class List and exactly one type
     * argument. (Note that a helper method with a simplified return type exists for the special case of one type
     * argument). It would then call this method, and in return get the type arguments which contain the element type
     * of the list.
     * <p>
     * Returns null if the type is not as expected.
     *
     * @param type the type to check
     * @param expectedRawClass the expected raw class which the type must use
     * @param expectedNumberOfTypeArguments the expected number of type arguments
     * @return the type arguments, or null if the type is not as expected
     */
    public static Type[] isParameterizedType(Type type, Class<?> expectedRawClass, int expectedNumberOfTypeArguments) {
        if (type instanceof ParameterizedType p && p.getRawType().equals(expectedRawClass)) {
            Type[] typeArguments = p.getActualTypeArguments();
            if (typeArguments.length == expectedNumberOfTypeArguments) {
                return typeArguments;
            }
        }
        return null;
    }

    /**
     * Like isParameterizedType(), but with expectedNumberOfTypeArguments=1 and returns the single type argument
     * directly, not as an array.
     *
     * @param type the type to check
     * @param expectedRawClass the expected raw class which the type must use
     * @return the type argument, or null if the type is not as expected
     */
    public static Type isSingleParameterizedType(Type type, Class<?> expectedRawClass) {
        Type[] result = isParameterizedType(type, expectedRawClass, 1);
        return result == null ? null : result[0];
    }

    /**
     * Like isParameterizedType(), but will fail with a RuntimeException if the type is not as expected, indicating
     * an internal bug in the type adapter.
     *
     * @param type the type to check
     * @param expectedRawClass the expected raw class which the type must use
     * @param expectedNumberOfTypeArguments the expected number of type arguments
     * @return the type arguments
     */
    public static Type[] expectParameterizedType(Type type, Class<?> expectedRawClass, int expectedNumberOfTypeArguments) {
        Type[] result = isParameterizedType(type, expectedRawClass, expectedNumberOfTypeArguments);
        if (result == null) {
            throw new RuntimeException("paramterized type not as expected");
        }
        return result;
    }

    /**
     * Like isSingleParameterizedType(), but will fail with a RuntimeException if the type is not as expected,
     * indicating an internal bug in the type adapter.
     *
     * @param type the type to check
     * @param expectedRawClass the expected raw class which the type must use
     * @return the type argument
     */
    public static Type expectSingleParameterizedType(Type type, Class<?> expectedRawClass) {
        Type result = isSingleParameterizedType(type, expectedRawClass);
        if (result == null) {
            throw new RuntimeException("paramterized type not as expected");
        }
        return result;
    }
}
