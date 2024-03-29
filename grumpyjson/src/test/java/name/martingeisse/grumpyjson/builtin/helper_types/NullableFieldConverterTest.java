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

    private final JsonRegistries registries = createRegistries(new IntegerConverter(), new StringConverter());
    private final NullableFieldConverter converter = new NullableFieldConverter(registries);

    public NullableFieldConverterTest() {
        registries.seal();
    }

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
        assertFalse(converter.supportsTypeForDeserialization(NullableField.class));
        assertFalse(converter.supportsTypeForDeserialization(new TypeToken<NullableField>() {}.getType()));
        assertTrue(converter.supportsTypeForDeserialization(new TypeToken<NullableField<?>>() {}.getType()));
        assertTrue(converter.supportsTypeForDeserialization(new TypeToken<NullableField<Integer>>() {}.getType()));
        assertTrue(converter.supportsTypeForDeserialization(new TypeToken<NullableField<OutputStream>>() {}.getType()));
    }

    @Test
    public void testDeserializationWrongType() throws Exception {
        forBooleans(json -> assertFailsDeserialization(converter, json, NULLABLE_INTEGER_TYPE));
        forStrings(json -> assertFailsDeserialization(converter, json, NULLABLE_INTEGER_TYPE));
        forObjects(json -> assertFailsDeserialization(converter, json, NULLABLE_INTEGER_TYPE));
    }

    @Test
    public void testSerializationHappyCase() {
        assertEquals(JsonNull.INSTANCE, converter.serialize(NullableField.ofNull()));
        assertEquals(new JsonPrimitive(12), converter.serialize(NullableField.ofValue(12)));
        assertEquals(new JsonPrimitive("foo"), converter.serialize(NullableField.ofValue("foo")));
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(converter, null);
    }

    @Test
    public void testDoesNotSupportAbsentFields() {
        assertEquals(Optional.of(JsonNull.INSTANCE), converter.serializeOptional(NullableField.ofNull()));
        assertEquals(Optional.of(new JsonPrimitive(12)), converter.serializeOptional(NullableField.ofValue(12)));
        assertThrows(JsonDeserializationException.class, () -> converter.deserializeAbsent(NULLABLE_INTEGER_TYPE));
    }

}
