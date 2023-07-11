/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
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

public class NullableFieldConverterTest {

    private static final Type NULLABLE_INTEGER_TYPE = new TypeToken<NullableField<Integer>>() {}.getType();
    private static final Type NULLABLE_STRING_TYPE = new TypeToken<NullableField<String>>() {}.getType();

    private final JsonRegistries registries = createRegistry(new IntegerConverter(), new StringConverter());
    private final NullableFieldConverter converter = new NullableFieldConverter(registries);

    @Test
    public void testDeserializationHappyCase() throws Exception {
        assertEquals(NullableField.ofNull(), converter.deserialize(JsonNull.INSTANCE, NULLABLE_INTEGER_TYPE));
        assertEquals(NullableField.ofValue(12), converter.deserialize(new JsonPrimitive(12), NULLABLE_INTEGER_TYPE));
        assertEquals(NullableField.ofNull(), converter.deserialize(JsonNull.INSTANCE, NULLABLE_STRING_TYPE));
        assertEquals(NullableField.ofValue("foo"), converter.deserialize(new JsonPrimitive("foo"), NULLABLE_STRING_TYPE));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testUnboundTypeParameter() {
        // see ListAdapterTest for information on *why* things are expected to behave this way
        assertFalse(converter.supportsType(NullableField.class));
        assertFalse(converter.supportsType(new TypeToken<NullableField>() {
        }.getType()));
        assertTrue(converter.supportsType(new TypeToken<NullableField<?>>() {
        }.getType()));
        assertTrue(converter.supportsType(new TypeToken<NullableField<Integer>>() {
        }.getType()));
        assertTrue(converter.supportsType(new TypeToken<NullableField<OutputStream>>() {
        }.getType()));
    }

    @Test
    public void testDeserializationWrongType() {
        forBooleans(json -> assertFailsDeserialization(converter, json, NULLABLE_INTEGER_TYPE));
        forStrings(json -> assertFailsDeserialization(converter, json, NULLABLE_INTEGER_TYPE));
        forObjects(json -> assertFailsDeserialization(converter, json, NULLABLE_INTEGER_TYPE));
    }

    @Test
    public void testSerializationHappyCase() {
        assertEquals(JsonNull.INSTANCE, converter.serialize(NullableField.ofNull(), NULLABLE_INTEGER_TYPE));
        assertEquals(new JsonPrimitive(12), converter.serialize(NullableField.ofValue(12), NULLABLE_INTEGER_TYPE));

        assertEquals(JsonNull.INSTANCE, converter.serialize(NullableField.ofNull(), NULLABLE_STRING_TYPE));
        assertEquals(new JsonPrimitive("foo"), converter.serialize(NullableField.ofValue("foo"), NULLABLE_STRING_TYPE));
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(converter, null, NULLABLE_INTEGER_TYPE);
    }

    @Test
    public void testSerializationWithWrongType() {
        assertFailsSerialization(converter, NullableField.ofValue(12), NULLABLE_STRING_TYPE);
        assertFailsSerialization(converter, NullableField.ofValue("foo"), NULLABLE_INTEGER_TYPE);
    }

    @Test
    public void testDoesNotSupportAbsentFields() {
        assertEquals(Optional.of(JsonNull.INSTANCE), converter.serializeOptional(NullableField.ofNull(), NULLABLE_INTEGER_TYPE));
        assertEquals(Optional.of(new JsonPrimitive(12)), converter.serializeOptional(NullableField.ofValue(12), NULLABLE_INTEGER_TYPE));
        assertThrows(JsonDeserializationException.class, () -> converter.deserializeAbsent(NULLABLE_INTEGER_TYPE));
    }

}
