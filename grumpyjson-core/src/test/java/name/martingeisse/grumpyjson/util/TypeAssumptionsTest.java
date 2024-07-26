/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SuppressWarnings({"InstantiationOfUtilityClass", "unused"})
class NoParameterOuter {

    static class StaticInner<B> {
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    class NonstaticInner<B> {
    }

    static final NoParameterOuter.StaticInner<String> staticInner = new NoParameterOuter.StaticInner<>();
    static final NoParameterOuter outer = new NoParameterOuter();
    static final NoParameterOuter.NonstaticInner<String> nonstaticInner = outer.new NonstaticInner<>();

}

@SuppressWarnings({"InstantiationOfUtilityClass", "unused"})
class ParameterOuter<A> {

    static class StaticInner<B> {
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    class NonstaticInner<B> {
    }

    static final ParameterOuter.StaticInner<String> staticInner = new StaticInner<>();
    static final ParameterOuter<Integer> outer = new ParameterOuter<>();
    static final ParameterOuter<Integer>.NonstaticInner<String> nonstaticInner = outer.new NonstaticInner<>();
}

public class TypeAssumptionsTest {

    @Test
    public void testAssumptionsForNoParameterOuterStaticInner() throws Exception {
        Field staticInnerField = NoParameterOuter.class.getDeclaredField("staticInner");
        ParameterizedType staticInnerType = (ParameterizedType) staticInnerField.getGenericType();

        // the owner of a static nested class is its _raw_ enclosing class
        assertEquals(NoParameterOuter.class, staticInnerType.getOwnerType());

        // the raw type and type arguments work like for a toplevel class
        assertEquals(NoParameterOuter.StaticInner.class, staticInnerType.getRawType());
        assertEquals(1, staticInnerType.getActualTypeArguments().length);
        assertEquals(String.class, staticInnerType.getActualTypeArguments()[0]);

    }

    @Test
    public void testAssumptionsForNoParameterOuterNonstaticInner() throws Exception {
        Field nonstaticInnerField = NoParameterOuter.class.getDeclaredField("nonstaticInner");
        ParameterizedType nonstaticInnerType = (ParameterizedType) nonstaticInnerField.getGenericType();

        // the enclosing class is raw in any case because it is defined that way
        assertEquals(NoParameterOuter.class, nonstaticInnerType.getOwnerType());

        // therefore, the raw type and type arguments work like for a toplevel class
        assertEquals(NoParameterOuter.NonstaticInner.class, nonstaticInnerType.getRawType());
        assertEquals(1, nonstaticInnerType.getActualTypeArguments().length);
        assertEquals(String.class, nonstaticInnerType.getActualTypeArguments()[0]);

    }

    /*
    This works very much like NoParameterOuter.StaticInner because the outer class's parameter does not affect
    static nested classes.
     */
    @Test
    public void testAssumptionsForParameterOuterStaticInner() throws Exception {
        Field staticInnerField = ParameterOuter.class.getDeclaredField("staticInner");
        ParameterizedType staticInnerType = (ParameterizedType) staticInnerField.getGenericType();

        // the owner of a static nested class is its _raw_ enclosing class
        assertEquals(ParameterOuter.class, staticInnerType.getOwnerType());

        // the raw type and type arguments work like for a toplevel class
        assertEquals(ParameterOuter.StaticInner.class, staticInnerType.getRawType());
        assertEquals(1, staticInnerType.getActualTypeArguments().length);
        assertEquals(String.class, staticInnerType.getActualTypeArguments()[0]);

    }

    @Test
    public void testAssumptionsForParameterOuterNonstaticInner() throws Exception {
        Field nonstaticInnerField = ParameterOuter.class.getDeclaredField("nonstaticInner");
        ParameterizedType nonstaticInnerType = (ParameterizedType) nonstaticInnerField.getGenericType();

        // this time, the owner is itself a parameterized type
        ParameterizedType ownerType = (ParameterizedType) nonstaticInnerType.getOwnerType();
        assertNull(ownerType.getOwnerType());
        assertEquals(ParameterOuter.class, ownerType.getRawType());
        assertEquals(1, ownerType.getActualTypeArguments().length);
        assertEquals(Integer.class, ownerType.getActualTypeArguments()[0]);

        // since the binding of the outer class's type parameter happens in the owner, the field type only binds
        // the type parameter of the inner class
        assertEquals(ParameterOuter.NonstaticInner.class, nonstaticInnerType.getRawType());
        assertEquals(1, nonstaticInnerType.getActualTypeArguments().length);
        assertEquals(String.class, nonstaticInnerType.getActualTypeArguments()[0]);

    }

}
