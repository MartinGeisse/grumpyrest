/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;

public class BooleanConverterTest {

    private final BooleanConverter adapter = new BooleanConverter();

    @Test
    public void testDeserializationHappyCase() throws Exception {
        Assertions.assertEquals(true, adapter.deserialize(new JsonPrimitive(true), Boolean.TYPE));
        Assertions.assertEquals(false, adapter.deserialize(new JsonPrimitive(false), Boolean.TYPE));
    }

    @Test
    public void testDeserializationWrongType() {
        forNonPrimitive(json -> assertFailsDeserialization(adapter, json, Boolean.TYPE));
        forNull(json -> assertFailsDeserialization(adapter, json, Boolean.TYPE));
        forNumbers(json -> assertFailsDeserialization(adapter, json, Boolean.TYPE));
        forStrings(json -> assertFailsDeserialization(adapter, json, Boolean.TYPE));
    }

    @Test
    public void testSerializationHappyCase() {
        Assertions.assertEquals(new JsonPrimitive(true), adapter.serialize(true, Boolean.TYPE));
        Assertions.assertEquals(new JsonPrimitive(false), adapter.serialize(false, Boolean.TYPE));
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(adapter, null, Boolean.TYPE);
    }

}
