/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NullableFieldTest {

    private final NullableField<String> ofNull = NullableField.ofNull();
    private final NullableField<String> ofValue = NullableField.ofValue("foo");

    @Test
    public void testGetters() {
        assertTrue(ofNull.isNull());
        assertFalse(ofNull.isNonNull());
        assertNull(ofNull.getValueOrNull());
        assertThrows(IllegalStateException.class, () -> ofNull.getValue());

        assertFalse(ofValue.isNull());
        assertTrue(ofValue.isNonNull());
        assertEquals("foo", ofValue.getValueOrNull());
        assertEquals("foo", ofValue.getValue());

        assertEquals("bar", ofNull.orElse("bar"));
        assertEquals("foo", ofValue.orElse("bar"));
        assertNull(ofNull.orElse(null));
        assertEquals("foo", ofValue.orElse(null));

        assertEquals("bar", ofNull.orElseGet(() -> "bar"));
        assertEquals("foo", ofValue.orElseGet(() -> "bar"));
        assertNull(ofNull.orElseGet(() -> null));
        assertEquals("foo", ofValue.orElseGet(() -> null));
        assertThrows(NullPointerException.class, () -> ofNull.orElseGet(null));
        assertThrows(NullPointerException.class, () -> ofValue.orElseGet(null));
    }

    @Test
    public void testFactoryMethods() {
        assertEquals("foo", NullableField.ofValue("foo").getValueOrNull());
        assertThrows(NullPointerException.class, () -> NullableField.ofValue(null));

        assertNull(NullableField.ofNull().getValueOrNull());

        assertEquals("foo", NullableField.ofValueOrNull("foo").getValueOrNull());
        assertNull(NullableField.ofValueOrNull(null).getValueOrNull());
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void testEqualsHashCodeToString() {
        assertEquals(NullableField.ofValue("foo"), NullableField.ofValue("foo"));
        assertNotEquals(NullableField.ofValue("foo"), NullableField.ofValue("bar"));
        assertNotEquals(NullableField.ofValue("foo"), NullableField.ofNull());
        assertNotEquals(NullableField.ofNull(), NullableField.ofValue("foo"));
        assertEquals(NullableField.ofNull(), NullableField.ofNull());

        assertEquals(NullableField.ofValue("foo").hashCode(), NullableField.ofValue("foo").hashCode());
        assertEquals(NullableField.ofNull().hashCode(), NullableField.ofNull().hashCode());

        //noinspection ResultOfMethodCallIgnored -- just checking that it does not crash
        NullableField.ofNull().toString();

        assertTrue(NullableField.ofValue("foo").toString().contains("foo"));
    }

}
