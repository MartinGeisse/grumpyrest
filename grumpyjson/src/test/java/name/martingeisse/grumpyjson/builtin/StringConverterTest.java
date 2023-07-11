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

public class StringConverterTest {

    private final StringConverter adapter = new StringConverter();

    @Test
    public void testDeserializationHappyCase() throws Exception {
        Assertions.assertEquals("", adapter.deserialize(new JsonPrimitive(""), String.class));
        Assertions.assertEquals("abc", adapter.deserialize(new JsonPrimitive("abc"), String.class));
    }

    @Test
    public void testDeserializationWrongType() {
        forNonPrimitive(json -> assertFailsDeserialization(adapter, json, String.class));
        forNull(json -> assertFailsDeserialization(adapter, json, String.class));
        forBooleans(json -> assertFailsDeserialization(adapter, json, String.class));
        forNumbers(json -> assertFailsDeserialization(adapter, json, String.class));
    }

    @Test
    public void testSerializationHappyCase() {
        Assertions.assertEquals(new JsonPrimitive("foo"), adapter.serialize("foo", String.class));
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(adapter, null, String.class);
    }

}
