/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import name.martingeisse.grumpyjson.JsonGenerationException;
import name.martingeisse.grumpyjson.JsonRegistry;
import name.martingeisse.grumpyjson.JsonValidationException;
import name.martingeisse.grumpyjson.builtin.IntegerAdapter;
import name.martingeisse.grumpyjson.builtin.StringAdapter;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Optional;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class JsonOptionalAdapterTest {

    private static final Type OPTIONAL_INTEGER_TYPE = new TypeToken<JsonOptional<Integer>>() {}.getType();
    private static final Type OPTIONAL_STRING_TYPE = new TypeToken<JsonOptional<String>>() {}.getType();

    private final JsonRegistry registry = createRegistry(new IntegerAdapter(), new StringAdapter());
    private final JsonOptionalAdapter adapter = new JsonOptionalAdapter(registry);

    @Test
    public void testValidationHappyCase() throws Exception {
        assertEquals(JsonOptional.ofNothing(), adapter.fromAbsentJson(OPTIONAL_INTEGER_TYPE));
        assertEquals(JsonOptional.ofValue(12), adapter.fromJson(new JsonPrimitive(12), OPTIONAL_INTEGER_TYPE));
        assertEquals(JsonOptional.ofNothing(), adapter.fromAbsentJson(OPTIONAL_STRING_TYPE));
        assertEquals(JsonOptional.ofValue("foo"), adapter.fromJson(new JsonPrimitive("foo"), OPTIONAL_STRING_TYPE));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testUnboundTypeParameter() {
        // see ImmuableAdapterListTest for information on *why* things are expected to behave this way
        assertFalse(adapter.supportsType(JsonOptional.class));
        assertFalse(adapter.supportsType(new TypeToken<JsonOptional>() {}.getType()));
        assertTrue(adapter.supportsType(new TypeToken<JsonOptional<?>>() {}.getType()));
        assertTrue(adapter.supportsType(new TypeToken<JsonOptional<Integer>>() {}.getType()));
        assertTrue(adapter.supportsType(new TypeToken<JsonOptional<OutputStream>>() {}.getType()));
    }

    @Test
    public void testValidationWrongType() {
        forNull(json -> assertFailsValidation(adapter, json, OPTIONAL_INTEGER_TYPE));
        forBooleans(json -> assertFailsValidation(adapter, json, OPTIONAL_INTEGER_TYPE));
        forStrings(json -> assertFailsValidation(adapter, json, OPTIONAL_INTEGER_TYPE));
        forObjects(json -> assertFailsValidation(adapter, json, OPTIONAL_INTEGER_TYPE));
    }

    @Test
    public void testGenerationHappyCase() {
        assertEquals(Optional.empty(), adapter.toOptionalJson(JsonOptional.ofNothing(), OPTIONAL_INTEGER_TYPE));
        assertEquals(Optional.of(new JsonPrimitive(12)), adapter.toOptionalJson(JsonOptional.ofValue(12), OPTIONAL_INTEGER_TYPE));

        assertEquals(Optional.empty(), adapter.toOptionalJson(JsonOptional.ofNothing(), OPTIONAL_STRING_TYPE));
        assertEquals(Optional.of(new JsonPrimitive("foo")), adapter.toOptionalJson(JsonOptional.ofValue("foo"), OPTIONAL_STRING_TYPE));
    }

    @Test
    public void testGenerationWithNull() {
        //noinspection DataFlowIssue
        assertThrows(NullPointerException.class, () -> adapter.toOptionalJson(null, OPTIONAL_INTEGER_TYPE));
    }

    @Test
    public void testGenerationWithWrongType() {
        assertThrows(JsonGenerationException.class, () -> adapter.toOptionalJson(JsonOptional.ofValue(12), OPTIONAL_STRING_TYPE));
        assertThrows(JsonGenerationException.class, () -> adapter.toOptionalJson(JsonOptional.ofValue("foo"), OPTIONAL_INTEGER_TYPE));
    }

    @Test
    public void testOnlyWorksInVanishableLocations() {
        assertThrows(JsonGenerationException.class, () -> adapter.toJson(JsonOptional.ofNothing(), OPTIONAL_INTEGER_TYPE));
        assertThrows(JsonGenerationException.class, () -> adapter.toJson(JsonOptional.ofValue(12), OPTIONAL_INTEGER_TYPE));
    }

    @Test
    public void testDoesNotSupportNull() {
        assertThrows(JsonValidationException.class, () -> adapter.fromJson(JsonNull.INSTANCE, OPTIONAL_INTEGER_TYPE));
    }


}
