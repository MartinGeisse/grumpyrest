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
    public void testValidationHappyCase() throws Exception {
        Assertions.assertEquals("", adapter.fromJson(new JsonPrimitive(""), String.class));
        Assertions.assertEquals("abc", adapter.fromJson(new JsonPrimitive("abc"), String.class));
    }

    @Test
    public void testValidationWrongType() {
        forNonPrimitive(json -> assertFailsValidation(adapter, json, String.class));
        forNull(json -> assertFailsValidation(adapter, json, String.class));
        forBooleans(json -> assertFailsValidation(adapter, json, String.class));
        forNumbers(json -> assertFailsValidation(adapter, json, String.class));
    }

    @Test
    public void testGenerationHappyCase() {
        Assertions.assertEquals(new JsonPrimitive("foo"), adapter.toJson("foo", String.class));
    }

    @Test
    public void testGenerationWithNull() {
        assertFailsGenerationWithNpe(adapter, null, String.class);
    }

}
