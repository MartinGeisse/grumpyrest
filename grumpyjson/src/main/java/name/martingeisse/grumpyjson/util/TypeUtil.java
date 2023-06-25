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

    private TypeUtil() {
    }

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
     * @param type                          the type to check
     * @param expectedRawClass              the expected raw class which the type must use
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
     * @param type             the type to check
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
     * @param type                          the type to check
     * @param expectedRawClass              the expected raw class which the type must use
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
     * @param type             the type to check
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

    /**
     * Returns a type like the original, but with all its type variables replaced by the bound types. This method
     * expects to get bindings for all type variables, and will throw an exception if it encounters a type variable
     * for which no binding was passed (simply to fail fast -- this method itself could leave unbound type variables
     * in the result, but is not expected to be used that way).
     * <p>
     * This method does not check if the types from the bindings contain variables, and if they do, these variables
     * will be part of the result. However, this method is not expected to be used that way, reflecting how type
     * parameters get bound: For a generic type <code>MyType&lt;A,B&gt;</code>, you cannot build a parameterized
     * version of that type like <code>MyType&lt;String,List&lt;A&gt;&gt;</code> -- that is, the binding for type
     * parameter B cannot use the type variable A to define its bound type.
     * <p>
     * This method can only handle three kinds of types, raw class objects, {@link ParameterizedType} and, of course,
     * type variables. Any other type will cause an exception. The reason is that these cases are not needed by
     * grumpyjson because they cause other problems downstream -- fields using such types cannot be converted from
     * JSON because the type information is not sufficient. However, they can occur due to user error and therefore
     * should produce a clean error message.
     *
     * @param original the original type to replace type variables in
     * @param bindings the bindings of variables to types
     * @return the type with type variables replaced
     */
    public static Type replaceTypeVariables(Type original, Map<String, Type> bindings) {
        if (original instanceof Class<?>) {
            return original;
        } else if (original instanceof ParameterizedType parameterized) {
            // Apache Commons uses parameterized.getRawType().getEnclosingClass() as a fallback if the owner is null.
            // I'm not sure why, as I think that if there is an enclosing class, then the original type should have an
            // owner too. I'm leaving this note just in case this works differently, so I have a hint how to solve it.
            Type ownerBeforeReplacement = parameterized.getOwnerType();
            Type ownerAfterReplacement = ownerBeforeReplacement == null ? null : replaceTypeVariables(ownerBeforeReplacement, bindings);
            Type[] argumentsBeforeReplacement = parameterized.getActualTypeArguments();
            Type[] argumentsAfterReplacement = new Type[argumentsBeforeReplacement.length];
            for (int i = 0; i < argumentsBeforeReplacement.length; i++) {
                // This recursion _is_ necessary. If a field uses type List<List<A>>, and we have a binding A->String,
                // then the result should be List<List<String>>.
                argumentsAfterReplacement[i] = replaceTypeVariables(argumentsBeforeReplacement[i], bindings);
            }
            return new ParameterizedTypeImpl(ownerAfterReplacement, parameterized.getRawType(), argumentsAfterReplacement);
        } else if (original instanceof TypeVariable<?> variable) {
            Type boundType = bindings.get(variable.getName());
            if (boundType == null) {
                throw new IllegalArgumentException("no binding for type variable " + variable.getName());
            }
            return boundType;
        } else {
            throw new IllegalArgumentException("found unexpected type: " + original);
        }
    }

}
