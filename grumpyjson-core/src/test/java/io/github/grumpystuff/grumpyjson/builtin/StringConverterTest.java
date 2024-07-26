/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.builtin;

import io.github.grumpystuff.grumpyjson.json_model.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.github.grumpystuff.grumpyjson.JsonTestUtil.*;

public class StringConverterTest {

    private final StringConverter converter = new StringConverter();

    @Test
    public void testDeserializationHappyCase() throws Exception {
        Assertions.assertEquals("", converter.deserialize(JsonString.of(""), String.class));
        Assertions.assertEquals("abc", converter.deserialize(JsonString.of("abc"), String.class));
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
        Assertions.assertEquals(JsonString.of("foo"), converter.serialize("foo"));
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(converter, null);
    }

}
