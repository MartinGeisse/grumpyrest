/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonNullableTest {

    private final JsonNullable<String> ofNull = JsonNullable.ofNull();
    private final JsonNullable<String> ofValue = JsonNullable.ofValue("foo");

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
    }

    @Test
    public void testFactoryMethods() {
        assertEquals("foo", JsonNullable.ofValue("foo").getValueOrNull());
        assertThrows(NullPointerException.class, () -> JsonNullable.ofValue(null));

        assertNull(JsonNullable.ofNull().getValueOrNull());

        assertEquals("foo", JsonNullable.ofValueOrNull("foo").getValueOrNull());
        assertNull(JsonNullable.ofValueOrNull(null).getValueOrNull());
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void testEqualsHashCodeToString() {
        assertEquals(JsonNullable.ofValue("foo"), JsonNullable.ofValue("foo"));
        assertNotEquals(JsonNullable.ofValue("foo"), JsonNullable.ofValue("bar"));
        assertNotEquals(JsonNullable.ofValue("foo"), JsonNullable.ofNull());
        assertNotEquals(JsonNullable.ofNull(), JsonNullable.ofValue("foo"));
        assertEquals(JsonNullable.ofNull(), JsonNullable.ofNull());

        assertEquals(JsonNullable.ofValue("foo").hashCode(), JsonNullable.ofValue("foo").hashCode());
        assertEquals(JsonNullable.ofNull().hashCode(), JsonNullable.ofNull().hashCode());

        //noinspection ResultOfMethodCallIgnored -- just checking that it does not crash
        JsonNullable.ofNull().toString();

        assertTrue(JsonNullable.ofValue("foo").toString().contains("foo"));
    }

}
