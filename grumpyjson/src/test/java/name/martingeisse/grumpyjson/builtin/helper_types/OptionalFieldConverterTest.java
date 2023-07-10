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
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.JsonRegistry;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.builtin.IntegerConverter;
import name.martingeisse.grumpyjson.builtin.StringConverter;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Optional;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class OptionalFieldConverterTest {

    private static final Type OPTIONAL_INTEGER_TYPE = new TypeToken<OptionalField<Integer>>() {}.getType();
    private static final Type OPTIONAL_STRING_TYPE = new TypeToken<OptionalField<String>>() {}.getType();

    private final JsonRegistry registry = createRegistry(new IntegerConverter(), new StringConverter());
    private final OptionalFieldConverter adapter = new OptionalFieldConverter(registry);

    @Test
    public void testValidationHappyCase() throws Exception {
        assertEquals(OptionalField.ofNothing(), adapter.deserializeAbsent(OPTIONAL_INTEGER_TYPE));
        assertEquals(OptionalField.ofValue(12), adapter.deserialize(new JsonPrimitive(12), OPTIONAL_INTEGER_TYPE));
        assertEquals(OptionalField.ofNothing(), adapter.deserializeAbsent(OPTIONAL_STRING_TYPE));
        assertEquals(OptionalField.ofValue("foo"), adapter.deserialize(new JsonPrimitive("foo"), OPTIONAL_STRING_TYPE));
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
        assertEquals(Optional.empty(), adapter.serializeOptional(OptionalField.ofNothing(), OPTIONAL_INTEGER_TYPE));
        assertEquals(Optional.of(new JsonPrimitive(12)), adapter.serializeOptional(OptionalField.ofValue(12), OPTIONAL_INTEGER_TYPE));

        assertEquals(Optional.empty(), adapter.serializeOptional(OptionalField.ofNothing(), OPTIONAL_STRING_TYPE));
        assertEquals(Optional.of(new JsonPrimitive("foo")), adapter.serializeOptional(OptionalField.ofValue("foo"), OPTIONAL_STRING_TYPE));
    }

    @Test
    public void testGenerationWithNull() {
        //noinspection DataFlowIssue
        assertThrows(NullPointerException.class, () -> adapter.serializeOptional(null, OPTIONAL_INTEGER_TYPE));
    }

    @Test
    public void testGenerationWithWrongType() {
        assertThrows(JsonSerializationException.class, () -> adapter.serializeOptional(OptionalField.ofValue(12), OPTIONAL_STRING_TYPE));
        assertThrows(JsonSerializationException.class, () -> adapter.serializeOptional(OptionalField.ofValue("foo"), OPTIONAL_INTEGER_TYPE));
    }

    @Test
    public void testOnlyWorksInVanishableLocations() {
        assertThrows(JsonSerializationException.class, () -> adapter.serialize(OptionalField.ofNothing(), OPTIONAL_INTEGER_TYPE));
        assertThrows(JsonSerializationException.class, () -> adapter.serialize(OptionalField.ofValue(12), OPTIONAL_INTEGER_TYPE));
    }

    @Test
    public void testDoesNotSupportNull() {
        assertThrows(JsonDeserializationException.class, () -> adapter.deserialize(JsonNull.INSTANCE, OPTIONAL_INTEGER_TYPE));
    }


}
