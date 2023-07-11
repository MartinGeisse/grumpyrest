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

    private final FieldMustBeNullConverter adapter = new FieldMustBeNullConverter();

    @Test
    public void testValidationHappyCase() throws Exception {
        Assertions.assertEquals(FieldMustBeNull.INSTANCE, adapter.deserialize(JsonNull.INSTANCE, FieldMustBeNull.class));
    }

    @Test
    public void testValidationWrongType() {
        forNonPrimitive(json -> assertFailsDeserialization(adapter, json, Integer.TYPE));
        forBooleans(json -> assertFailsDeserialization(adapter, json, Integer.TYPE));
        forNumbers(json -> assertFailsDeserialization(adapter, json, Integer.TYPE));
        forStrings(json -> assertFailsDeserialization(adapter, json, Integer.TYPE));
    }

    @Test
    public void testGenerationHappyCase() {
        Assertions.assertEquals(JsonNull.INSTANCE, adapter.serialize(FieldMustBeNull.INSTANCE, FieldMustBeNull.class));
    }

    @Test
    public void testGenerationWithNull() {
        assertFailsGenerationWithNpe(adapter, null, FieldMustBeNull.class);
    }

}
