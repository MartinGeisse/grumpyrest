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
import name.martingeisse.grumpyjson.JsonRegistries;
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

    private final JsonRegistries registries = createRegistry(new IntegerConverter(), new StringConverter());
    private final OptionalFieldConverter converter = new OptionalFieldConverter(registries);

    @Test
    public void testDeserializationHappyCase() throws Exception {
        assertEquals(OptionalField.ofNothing(), converter.deserializeAbsent(OPTIONAL_INTEGER_TYPE));
        assertEquals(OptionalField.ofValue(12), converter.deserialize(new JsonPrimitive(12), OPTIONAL_INTEGER_TYPE));
        assertEquals(OptionalField.ofNothing(), converter.deserializeAbsent(OPTIONAL_STRING_TYPE));
        assertEquals(OptionalField.ofValue("foo"), converter.deserialize(new JsonPrimitive("foo"), OPTIONAL_STRING_TYPE));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testUnboundTypeParameter() {
        // see ListAdapterTest for information on *why* things are expected to behave this way
        assertFalse(converter.supportsType(OptionalField.class));
        assertFalse(converter.supportsType(new TypeToken<OptionalField>() {}.getType()));
        assertTrue(converter.supportsType(new TypeToken<OptionalField<?>>() {}.getType()));
        assertTrue(converter.supportsType(new TypeToken<OptionalField<Integer>>() {}.getType()));
        assertTrue(converter.supportsType(new TypeToken<OptionalField<OutputStream>>() {}.getType()));
    }

    @Test
    public void testDeserializationWrongType() {
        forNull(json -> assertFailsDeserialization(converter, json, OPTIONAL_INTEGER_TYPE));
        forBooleans(json -> assertFailsDeserialization(converter, json, OPTIONAL_INTEGER_TYPE));
        forStrings(json -> assertFailsDeserialization(converter, json, OPTIONAL_INTEGER_TYPE));
        forObjects(json -> assertFailsDeserialization(converter, json, OPTIONAL_INTEGER_TYPE));
    }

    @Test
    public void testSerializationHappyCase() {
        assertEquals(Optional.empty(), converter.serializeOptional(OptionalField.ofNothing(), OPTIONAL_INTEGER_TYPE));
        assertEquals(Optional.of(new JsonPrimitive(12)), converter.serializeOptional(OptionalField.ofValue(12), OPTIONAL_INTEGER_TYPE));

        assertEquals(Optional.empty(), converter.serializeOptional(OptionalField.ofNothing(), OPTIONAL_STRING_TYPE));
        assertEquals(Optional.of(new JsonPrimitive("foo")), converter.serializeOptional(OptionalField.ofValue("foo"), OPTIONAL_STRING_TYPE));
    }

    @Test
    public void testSerializationWithNull() {
        //noinspection DataFlowIssue
        assertThrows(NullPointerException.class, () -> converter.serializeOptional(null, OPTIONAL_INTEGER_TYPE));
    }

    @Test
    public void testSerializationWithWrongType() {
        assertThrows(JsonSerializationException.class, () -> converter.serializeOptional(OptionalField.ofValue(12), OPTIONAL_STRING_TYPE));
        assertThrows(JsonSerializationException.class, () -> converter.serializeOptional(OptionalField.ofValue("foo"), OPTIONAL_INTEGER_TYPE));
    }

    @Test
    public void testOnlyWorksInVanishableLocations() {
        assertThrows(JsonSerializationException.class, () -> converter.serialize(OptionalField.ofNothing(), OPTIONAL_INTEGER_TYPE));
        assertThrows(JsonSerializationException.class, () -> converter.serialize(OptionalField.ofValue(12), OPTIONAL_INTEGER_TYPE));
    }

    @Test
    public void testDoesNotSupportNull() {
        assertThrows(JsonDeserializationException.class, () -> converter.deserialize(JsonNull.INSTANCE, OPTIONAL_INTEGER_TYPE));
    }

}
