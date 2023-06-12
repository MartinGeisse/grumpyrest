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

public class BooleanAdapterTest {

    private final BooleanAdapter adapter = new BooleanAdapter();

    @Test
    public void testValidationHappyCase() throws Exception {
        Assertions.assertEquals(true, adapter.fromJson(new JsonPrimitive(true), Boolean.TYPE));
        Assertions.assertEquals(false, adapter.fromJson(new JsonPrimitive(false), Boolean.TYPE));
    }

    @Test
    public void testValidationWrongType() {
        forNonPrimitive(json -> assertFailsValidation(adapter, json, Boolean.TYPE));
        forNull(json -> assertFailsValidation(adapter, json, Boolean.TYPE));
        forNumbers(json -> assertFailsValidation(adapter, json, Boolean.TYPE));
        forStrings(json -> assertFailsValidation(adapter, json, Boolean.TYPE));
    }

    @Test
    public void testGenerationHappyCase() {
        Assertions.assertEquals(new JsonPrimitive(true), adapter.toJson(true, Boolean.TYPE));
        Assertions.assertEquals(new JsonPrimitive(false), adapter.toJson(false, Boolean.TYPE));
    }

    @Test
    public void testGenerationWithNull() {
        assertFailsGenerationWithNpe(adapter, null, Boolean.TYPE);
    }

}
