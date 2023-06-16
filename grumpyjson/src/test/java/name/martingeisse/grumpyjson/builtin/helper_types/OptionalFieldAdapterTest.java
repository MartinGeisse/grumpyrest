/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
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

public class OptionalFieldAdapterTest {

    private static final Type OPTIONAL_INTEGER_TYPE = new TypeToken<OptionalField<Integer>>() {}.getType();
    private static final Type OPTIONAL_STRING_TYPE = new TypeToken<OptionalField<String>>() {}.getType();

    private final JsonRegistry registry = createRegistry(new IntegerAdapter(), new StringAdapter());
    private final OptionalFieldAdapter adapter = new OptionalFieldAdapter(registry);

    @Test
    public void testValidationHappyCase() throws Exception {
        assertEquals(OptionalField.ofNothing(), adapter.fromAbsentJson(OPTIONAL_INTEGER_TYPE));
        assertEquals(OptionalField.ofValue(12), adapter.fromJson(new JsonPrimitive(12), OPTIONAL_INTEGER_TYPE));
        assertEquals(OptionalField.ofNothing(), adapter.fromAbsentJson(OPTIONAL_STRING_TYPE));
        assertEquals(OptionalField.ofValue("foo"), adapter.fromJson(new JsonPrimitive("foo"), OPTIONAL_STRING_TYPE));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testUnboundTypeParameter() {
        // see ListAdapterTest for information on *why* things are expected to behave this way
        assertFalse(adapter.supportsType(OptionalField.class));
        assertFalse(adapter.supportsType(new TypeToken<OptionalField>() {}.getType()));
        assertTrue(adapter.supportsType(new TypeToken<OptionalField<?>>() {}.getType()));
        assertTrue(adapter.supportsType(new TypeToken<OptionalField<Integer>>() {}.getType()));
        assertTrue(adapter.supportsType(new TypeToken<OptionalField<OutputStream>>() {}.getType()));
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
        assertEquals(Optional.empty(), adapter.toOptionalJson(OptionalField.ofNothing(), OPTIONAL_INTEGER_TYPE));
        assertEquals(Optional.of(new JsonPrimitive(12)), adapter.toOptionalJson(OptionalField.ofValue(12), OPTIONAL_INTEGER_TYPE));

        assertEquals(Optional.empty(), adapter.toOptionalJson(OptionalField.ofNothing(), OPTIONAL_STRING_TYPE));
        assertEquals(Optional.of(new JsonPrimitive("foo")), adapter.toOptionalJson(OptionalField.ofValue("foo"), OPTIONAL_STRING_TYPE));
    }

    @Test
    public void testGenerationWithNull() {
        //noinspection DataFlowIssue
        assertThrows(NullPointerException.class, () -> adapter.toOptionalJson(null, OPTIONAL_INTEGER_TYPE));
    }

    @Test
    public void testGenerationWithWrongType() {
        assertThrows(JsonGenerationException.class, () -> adapter.toOptionalJson(OptionalField.ofValue(12), OPTIONAL_STRING_TYPE));
        assertThrows(JsonGenerationException.class, () -> adapter.toOptionalJson(OptionalField.ofValue("foo"), OPTIONAL_INTEGER_TYPE));
    }

    @Test
    public void testOnlyWorksInVanishableLocations() {
        assertThrows(JsonGenerationException.class, () -> adapter.toJson(OptionalField.ofNothing(), OPTIONAL_INTEGER_TYPE));
        assertThrows(JsonGenerationException.class, () -> adapter.toJson(OptionalField.ofValue(12), OPTIONAL_INTEGER_TYPE));
    }

    @Test
    public void testDoesNotSupportNull() {
        assertThrows(JsonValidationException.class, () -> adapter.fromJson(JsonNull.INSTANCE, OPTIONAL_INTEGER_TYPE));
    }


}
