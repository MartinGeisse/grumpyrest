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

public class IntegerConverterTest {

    private final IntegerConverter adapter = new IntegerConverter();

    @Test
    public void testValidationHappyCase() throws Exception {
        Assertions.assertEquals(0, adapter.deserialize(new JsonPrimitive(0), Integer.TYPE));
        Assertions.assertEquals(123, adapter.deserialize(new JsonPrimitive(123), Integer.TYPE));
        Assertions.assertEquals(-123, adapter.deserialize(new JsonPrimitive(-123), Integer.TYPE));
    }

    @Test
    public void testValidationWrongType() {
        forNonPrimitive(json -> assertFailsDeserialization(adapter, json, Integer.TYPE));
        forNull(json -> assertFailsDeserialization(adapter, json, Integer.TYPE));
        forBooleans(json -> assertFailsDeserialization(adapter, json, Integer.TYPE));
        forStrings(json -> assertFailsDeserialization(adapter, json, Integer.TYPE));
    }

    @Test
    public void testValidationFloat() {
        assertFailsDeserialization(adapter, new JsonPrimitive(12.34), Integer.TYPE);
    }

    @Test
    public void testValidationSmallLong() throws Exception {
        Assertions.assertEquals(12, adapter.deserialize(new JsonPrimitive(12L), Integer.TYPE));
    }

    @Test
    public void testValidationTooLarge() {
        assertFailsDeserialization(adapter, new JsonPrimitive(0x80000000L), Integer.TYPE);
    }

    @Test
    public void testGenerationHappyCase() {
        Assertions.assertEquals(new JsonPrimitive(123), adapter.serialize(123, Integer.TYPE));
    }

    @Test
    public void testGenerationWithNull() {
        assertFailsGenerationWithNpe(adapter, null, Integer.TYPE);
    }

}
