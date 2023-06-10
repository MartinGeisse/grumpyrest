/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
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

public class NullableFieldAdapterTest {

    private static final Type NULLABLE_INTEGER_TYPE = new TypeToken<NullableField<Integer>>() {}.getType();
    private static final Type NULLABLE_STRING_TYPE = new TypeToken<NullableField<String>>() {}.getType();

    private final JsonRegistry registry = createRegistry(new IntegerAdapter(), new StringAdapter());
    private final NullableFieldAdapter adapter = new NullableFieldAdapter(registry);

    @Test
    public void testValidationHappyCase() throws Exception {
        assertEquals(NullableField.ofNull(), adapter.fromJson(JsonNull.INSTANCE, NULLABLE_INTEGER_TYPE));
        assertEquals(NullableField.ofValue(12), adapter.fromJson(new JsonPrimitive(12), NULLABLE_INTEGER_TYPE));
        assertEquals(NullableField.ofNull(), adapter.fromJson(JsonNull.INSTANCE, NULLABLE_STRING_TYPE));
        assertEquals(NullableField.ofValue("foo"), adapter.fromJson(new JsonPrimitive("foo"), NULLABLE_STRING_TYPE));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testUnboundTypeParameter() {
        // see ImmuableAdapterListTest for information on *why* things are expected to behave this way
        assertFalse(adapter.supportsType(NullableField.class));
        assertFalse(adapter.supportsType(new TypeToken<NullableField>() {
        }.getType()));
        assertTrue(adapter.supportsType(new TypeToken<NullableField<?>>() {
        }.getType()));
        assertTrue(adapter.supportsType(new TypeToken<NullableField<Integer>>() {
        }.getType()));
        assertTrue(adapter.supportsType(new TypeToken<NullableField<OutputStream>>() {
        }.getType()));
    }

    @Test
    public void testValidationWrongType() {
        forBooleans(json -> assertFailsValidation(adapter, json, NULLABLE_INTEGER_TYPE));
        forStrings(json -> assertFailsValidation(adapter, json, NULLABLE_INTEGER_TYPE));
        forObjects(json -> assertFailsValidation(adapter, json, NULLABLE_INTEGER_TYPE));
    }

    @Test
    public void testGenerationHappyCase() {
        assertEquals(JsonNull.INSTANCE, adapter.toJson(NullableField.ofNull(), NULLABLE_INTEGER_TYPE));
        assertEquals(new JsonPrimitive(12), adapter.toJson(NullableField.ofValue(12), NULLABLE_INTEGER_TYPE));

        assertEquals(JsonNull.INSTANCE, adapter.toJson(NullableField.ofNull(), NULLABLE_STRING_TYPE));
        assertEquals(new JsonPrimitive("foo"), adapter.toJson(NullableField.ofValue("foo"), NULLABLE_STRING_TYPE));
    }

    @Test
    public void testGenerationWithNull() {
        assertFailsGenerationWithNpe(adapter, null, NULLABLE_INTEGER_TYPE);
    }

    @Test
    public void testGenerationWithWrongType() {
        assertFailsGeneration(adapter, NullableField.ofValue(12), NULLABLE_STRING_TYPE);
        assertFailsGeneration(adapter, NullableField.ofValue("foo"), NULLABLE_INTEGER_TYPE);
    }

    @Test
    public void testDoesNotSupportAbsentFields() {
        assertEquals(Optional.of(JsonNull.INSTANCE), adapter.toOptionalJson(NullableField.ofNull(), NULLABLE_INTEGER_TYPE));
        assertEquals(Optional.of(new JsonPrimitive(12)), adapter.toOptionalJson(NullableField.ofValue(12), NULLABLE_INTEGER_TYPE));
        assertThrows(JsonValidationException.class, () -> adapter.fromAbsentJson(NULLABLE_INTEGER_TYPE));
    }

}
