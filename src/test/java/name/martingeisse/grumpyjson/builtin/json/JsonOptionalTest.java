/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonOptionalTest {

    private final JsonOptional<String> ofNothing = JsonOptional.ofNothing();
    private final JsonOptional<String> ofValue = JsonOptional.ofValue("foo");

    @Test
    public void testGetters() {
        assertTrue(ofNothing.isAbsent());
        assertFalse(ofNothing.isPresent());
        assertNull(ofNothing.getValueOrNothingAsNull());
        assertThrows(IllegalStateException.class, () -> ofNothing.getValue());

        assertFalse(ofValue.isAbsent());
        assertTrue(ofValue.isPresent());
        assertEquals("foo", ofValue.getValueOrNothingAsNull());
        assertEquals("foo", ofValue.getValue());
    }

    @Test
    public void testFactoryMethods() {
        assertEquals("foo", JsonOptional.ofValue("foo").getValueOrNothingAsNull());
        assertThrows(NullPointerException.class, () -> JsonOptional.ofValue(null));

        assertNull(JsonOptional.ofNothing().getValueOrNothingAsNull());

        assertEquals("foo", JsonOptional.ofValueOrNullAsNothing("foo").getValueOrNothingAsNull());
        assertNull(JsonOptional.ofValueOrNullAsNothing(null).getValueOrNothingAsNull());
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void testEqualsHashCodeToString() {
        assertEquals(JsonOptional.ofValue("foo"), JsonOptional.ofValue("foo"));
        assertNotEquals(JsonOptional.ofValue("foo"), JsonOptional.ofValue("bar"));
        assertNotEquals(JsonOptional.ofValue("foo"), JsonOptional.ofNothing());
        assertNotEquals(JsonOptional.ofNothing(), JsonOptional.ofValue("foo"));
        assertEquals(JsonOptional.ofNothing(), JsonOptional.ofNothing());

        assertEquals(JsonOptional.ofValue("foo").hashCode(), JsonOptional.ofValue("foo").hashCode());
        assertEquals(JsonOptional.ofNothing().hashCode(), JsonOptional.ofNothing().hashCode());

        //noinspection ResultOfMethodCallIgnored -- just checking that it does not crash
        JsonOptional.ofNothing().toString();

        assertTrue(JsonOptional.ofValue("foo").toString().contains("foo"));
    }

}
