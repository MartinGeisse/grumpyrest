/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import name.martingeisse.grumpyjson.JsonRegistry;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.lang.reflect.Type;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class ImmutableListAdapterTest {

    private static final Type INTEGER_LIST_TYPE = new TypeToken<ImmutableList<Integer>>() {}.getType();
    private static final Type STRING_LIST_TYPE = new TypeToken<ImmutableList<String>>() {}.getType();

    private final JsonRegistry registry = createRegistry(new IntegerAdapter(), new StringAdapter());
    private final ImmutableListAdapter adapter = new ImmutableListAdapter(registry);

    @Test
    public void testValidationHappyCase() throws Exception {
        assertEquals(ImmutableList.of(12, 34), adapter.fromJson(buildIntArray(12, 34), INTEGER_LIST_TYPE));
        assertEquals(ImmutableList.of("foo", "bar"), adapter.fromJson(buildStringArray("foo", "bar"), STRING_LIST_TYPE));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testUnboundTypeParameter() {

        // This type is not supported because it contains an unbound type parameter. In other words, grumpyjson does
        // not know which type to de-serialize the list elements as. It does not matter whether the type is specified
        // as a class object or as a type token.
        assertFalse(adapter.supportsType(ImmutableList.class));
        assertFalse(adapter.supportsType(new TypeToken<ImmutableList>() {}.getType()));

        // This is an edge case: the type parameter from ImmutableList is now bound, but it is bound to a wildcard.
        // The adapter will report this to be supported because we do not want to waste time to recursively check that
        // all contained types are supported -- adapter.supportsType() is meant to select the right adapter to use,
        // not to predict whether there will be any problems when using it. And there *will* be problems, because
        // actually using the adapter like this will try to get the (element) adapter for type "?" from the registry,
        // which does not exist.
        assertTrue(adapter.supportsType(new TypeToken<ImmutableList<?>>() {}.getType()));

        // When the type parameter is bound to a concrete type, then the type is supported.
        assertTrue(adapter.supportsType(new TypeToken<ImmutableList<Integer>>() {}.getType()));

        // Just like the wildcard case, the ImmutableList adapter does not care if the element type is a concrete type
        // for which there is no adapter.
        assertTrue(adapter.supportsType(new TypeToken<ImmutableList<OutputStream>>() {}.getType()));

    }

    @Test
    public void testValidationWrongType() {
        forNull(json -> assertFailsValidation(adapter, json, INTEGER_LIST_TYPE));
        forBooleans(json -> assertFailsValidation(adapter, json, INTEGER_LIST_TYPE));
        forNumbers(json -> assertFailsValidation(adapter, json, INTEGER_LIST_TYPE));
        forStrings(json -> assertFailsValidation(adapter, json, INTEGER_LIST_TYPE));
        forObjects(json -> assertFailsValidation(adapter, json, INTEGER_LIST_TYPE));
    }

    @Test
    public void testValidationWrongElementType() {
        assertFailsValidation(adapter, buildIntArray(12, 34), STRING_LIST_TYPE);
    }

    @Test
    public void testGenerationHappyCase() {
        assertEquals(buildIntArray(), adapter.toJson(ImmutableList.of(), INTEGER_LIST_TYPE));
        assertEquals(buildIntArray(12, 34), adapter.toJson(ImmutableList.of(12, 34), INTEGER_LIST_TYPE));
        assertEquals(buildIntArray(), adapter.toJson(ImmutableList.of(), STRING_LIST_TYPE));
        assertEquals(buildStringArray("foo", "bar"), adapter.toJson(ImmutableList.of("foo", "bar"), STRING_LIST_TYPE));
    }

    @Test
    public void testGenerationWithNull() {
        assertFailsGenerationWithNpe(adapter, null, INTEGER_LIST_TYPE);
    }

    @Test
    public void testGenerationWithWrongType() {
        assertFailsGeneration(adapter, ImmutableList.of(12, 34), STRING_LIST_TYPE);
        assertFailsGeneration(adapter, ImmutableList.of("foo", "bar"), INTEGER_LIST_TYPE);
    }

}
