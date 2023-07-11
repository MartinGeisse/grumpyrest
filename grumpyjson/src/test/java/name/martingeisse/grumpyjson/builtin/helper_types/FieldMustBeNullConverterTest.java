/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import com.google.gson.JsonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;

public class FieldMustBeNullConverterTest {

    private final FieldMustBeNullConverter converter = new FieldMustBeNullConverter();

    @Test
    public void testDeserializationHappyCase() throws Exception {
        Assertions.assertEquals(FieldMustBeNull.INSTANCE, converter.deserialize(JsonNull.INSTANCE, FieldMustBeNull.class));
    }

    @Test
    public void testDeserializationWrongType() {
        forNonPrimitive(json -> assertFailsDeserialization(converter, json, Integer.TYPE));
        forBooleans(json -> assertFailsDeserialization(converter, json, Integer.TYPE));
        forNumbers(json -> assertFailsDeserialization(converter, json, Integer.TYPE));
        forStrings(json -> assertFailsDeserialization(converter, json, Integer.TYPE));
    }

    @Test
    public void testSerializationHappyCase() {
        Assertions.assertEquals(JsonNull.INSTANCE, converter.serialize(FieldMustBeNull.INSTANCE));
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(converter, null);
    }

}
