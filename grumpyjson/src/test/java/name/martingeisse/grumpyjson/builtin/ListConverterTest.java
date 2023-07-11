/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyjson.JsonRegistries;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class ListConverterTest {

    private static final Type INTEGER_LIST_TYPE = new TypeToken<List<Integer>>() {}.getType();
    private static final Type STRING_LIST_TYPE = new TypeToken<List<String>>() {}.getType();

    private final JsonRegistries registries = createRegistry(new IntegerConverter(), new StringConverter());
    private final ListConverter adapter = new ListConverter(registries);

    @Test
    public void testDeserializationHappyCase() throws Exception {
        assertEquals(List.of(12, 34), adapter.deserialize(buildIntArray(12, 34), INTEGER_LIST_TYPE));
        assertEquals(List.of("foo", "bar"), adapter.deserialize(buildStringArray("foo", "bar"), STRING_LIST_TYPE));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testUnboundTypeParameter() {

        // This type is not supported because it contains an unbound type parameter. In other words, grumpyjson does
        // not know which type to de-serialize the list elements as. It does not matter whether the type is specified
        // as a class object or as a type token.
        assertFalse(adapter.supportsType(List.class));
        assertFalse(adapter.supportsType(new TypeToken<List>() {}.getType()));

        // This is an edge case: the type parameter from List is now bound, but it is bound to a wildcard.
        // The adapter will report this to be supported because we do not want to waste time to recursively check that
        // all contained types are supported -- adapter.supportsType() is meant to select the right adapter to use,
        // not to predict whether there will be any problems when using it. And there *will* be problems, because
        // actually using the adapter like this will try to get the (element) adapter for type "?" from the registry,
        // which does not exist.
        assertTrue(adapter.supportsType(new TypeToken<List<?>>() {}.getType()));

        // When the type parameter is bound to a concrete type, then the type is supported.
        assertTrue(adapter.supportsType(new TypeToken<List<Integer>>() {}.getType()));

        // Just like the wildcard case, the List adapter does not care if the element type is a concrete type
        // for which there is no adapter.
        assertTrue(adapter.supportsType(new TypeToken<List<OutputStream>>() {}.getType()));

    }

    @Test
    public void testDeserializationWrongType() {
        forNull(json -> assertFailsDeserialization(adapter, json, INTEGER_LIST_TYPE));
        forBooleans(json -> assertFailsDeserialization(adapter, json, INTEGER_LIST_TYPE));
        forNumbers(json -> assertFailsDeserialization(adapter, json, INTEGER_LIST_TYPE));
        forStrings(json -> assertFailsDeserialization(adapter, json, INTEGER_LIST_TYPE));
        forObjects(json -> assertFailsDeserialization(adapter, json, INTEGER_LIST_TYPE));
    }

    @Test
    public void testDeserializationWrongElementType() {
        assertFailsDeserialization(adapter, buildIntArray(12, 34), STRING_LIST_TYPE);
    }

    @Test
    public void testSerializationHappyCase() {
        assertEquals(buildIntArray(), adapter.serialize(List.of(), INTEGER_LIST_TYPE));
        assertEquals(buildIntArray(12, 34), adapter.serialize(List.of(12, 34), INTEGER_LIST_TYPE));
        assertEquals(buildIntArray(), adapter.serialize(List.of(), STRING_LIST_TYPE));
        assertEquals(buildStringArray("foo", "bar"), adapter.serialize(List.of("foo", "bar"), STRING_LIST_TYPE));
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(adapter, null, INTEGER_LIST_TYPE);
    }

    @Test
    public void testSerializationWithWrongType() {
        assertFailsSerialization(adapter, List.of(12, 34), STRING_LIST_TYPE);
        assertFailsSerialization(adapter, List.of("foo", "bar"), INTEGER_LIST_TYPE);
    }

}
