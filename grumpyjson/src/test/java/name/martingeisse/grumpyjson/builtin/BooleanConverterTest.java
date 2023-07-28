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

    private final BooleanConverter converter = new BooleanConverter();

    @Test
    public void testDeserializationHappyCase() throws Exception {
        Assertions.assertEquals(true, converter.deserialize(new JsonPrimitive(true), Boolean.TYPE));
        Assertions.assertEquals(false, converter.deserialize(new JsonPrimitive(false), Boolean.TYPE));
    }

    @Test
    public void testDeserializationWrongType() throws Exception {
        forNonPrimitive(json -> assertFailsDeserialization(converter, json, Boolean.TYPE));
        forNull(json -> assertFailsDeserialization(converter, json, Boolean.TYPE));
        forNumbers(json -> assertFailsDeserialization(converter, json, Boolean.TYPE));
        forStrings(json -> assertFailsDeserialization(converter, json, Boolean.TYPE));
    }

    @Test
    public void testSerializationHappyCase() {
        Assertions.assertEquals(new JsonPrimitive(true), converter.serialize(true));
        Assertions.assertEquals(new JsonPrimitive(false), converter.serialize(false));
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(converter, null);
    }

}
