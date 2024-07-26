/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.builtin.helper_types;

import io.github.grumpystuff.grumpyjson.json_model.JsonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.github.grumpystuff.grumpyjson.JsonTestUtil.*;

public class FieldMustBeNullConverterTest {

    private final FieldMustBeNullConverter converter = new FieldMustBeNullConverter();

    @Test
    public void testDeserializationHappyCase() throws Exception {
        Assertions.assertEquals(FieldMustBeNull.INSTANCE, converter.deserialize(JsonNull.INSTANCE, FieldMustBeNull.class));
    }

    @Test
    public void testDeserializationWrongType() throws Exception {
        forNonPrimitive(json -> assertFailsDeserialization(converter, json, FieldMustBeNull.class));
        forBooleans(json -> assertFailsDeserialization(converter, json, FieldMustBeNull.class));
        forNumbers(json -> assertFailsDeserialization(converter, json, FieldMustBeNull.class));
        forStrings(json -> assertFailsDeserialization(converter, json, FieldMustBeNull.class));
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
