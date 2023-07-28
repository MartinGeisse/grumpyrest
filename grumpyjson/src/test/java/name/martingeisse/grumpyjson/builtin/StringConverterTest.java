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

    private final StringConverter converter = new StringConverter();

    @Test
    public void testDeserializationHappyCase() throws Exception {
        Assertions.assertEquals("", converter.deserialize(new JsonPrimitive(""), String.class));
        Assertions.assertEquals("abc", converter.deserialize(new JsonPrimitive("abc"), String.class));
    }

    @Test
    public void testDeserializationWrongType() throws Exception {
        forNonPrimitive(json -> assertFailsDeserialization(converter, json, String.class));
        forNull(json -> assertFailsDeserialization(converter, json, String.class));
        forBooleans(json -> assertFailsDeserialization(converter, json, String.class));
        forNumbers(json -> assertFailsDeserialization(converter, json, String.class));
    }

    @Test
    public void testSerializationHappyCase() {
        Assertions.assertEquals(new JsonPrimitive("foo"), converter.serialize("foo"));
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(converter, null);
    }

}
