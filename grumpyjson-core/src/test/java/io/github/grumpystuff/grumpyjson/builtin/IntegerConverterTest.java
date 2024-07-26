/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.builtin;

import io.github.grumpystuff.grumpyjson.json_model.JsonNumber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.github.grumpystuff.grumpyjson.JsonTestUtil.*;

public class IntegerConverterTest {

    private final IntegerConverter converter = new IntegerConverter();

    @Test
    public void testDeserializationHappyCase() throws Exception {
        Assertions.assertEquals(0, converter.deserialize(JsonNumber.of(0), Integer.TYPE));
        Assertions.assertEquals(123, converter.deserialize(JsonNumber.of(123), Integer.TYPE));
        Assertions.assertEquals(-123, converter.deserialize(JsonNumber.of(-123), Integer.TYPE));
    }

    @Test
    public void testDeserializationWrongType() throws Exception {
        forNonPrimitive(json -> assertFailsDeserialization(converter, json, Integer.TYPE));
        forNull(json -> assertFailsDeserialization(converter, json, Integer.TYPE));
        forBooleans(json -> assertFailsDeserialization(converter, json, Integer.TYPE));
        forStrings(json -> assertFailsDeserialization(converter, json, Integer.TYPE));
    }

    @Test
    public void testDeserializationFloat() {
        assertFailsDeserialization(converter, JsonNumber.of(12.34), Integer.TYPE);
    }

    @Test
    public void testDeserializationSmallLong() throws Exception {
        Assertions.assertEquals(12, converter.deserialize(JsonNumber.of(12L), Integer.TYPE));
    }

    @Test
    public void testDeserializationTooLarge() {
        assertFailsDeserialization(converter, JsonNumber.of(0x80000000L), Integer.TYPE);
    }

    @Test
    public void testSerializationHappyCase() {
        Assertions.assertEquals(JsonNumber.of(123), converter.serialize(123));
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(converter, null);
    }

}
