/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalFieldTest {

    private final OptionalField<String> ofNothing = OptionalField.ofNothing();
    private final OptionalField<String> ofValue = OptionalField.ofValue("foo");

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
        assertEquals("foo", OptionalField.ofValue("foo").getValueOrNothingAsNull());
        assertThrows(NullPointerException.class, () -> OptionalField.ofValue(null));

        assertNull(OptionalField.ofNothing().getValueOrNothingAsNull());

        assertEquals("foo", OptionalField.ofValueOrNullAsNothing("foo").getValueOrNothingAsNull());
        assertNull(OptionalField.ofValueOrNullAsNothing(null).getValueOrNothingAsNull());
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void testEqualsHashCodeToString() {
        assertEquals(OptionalField.ofValue("foo"), OptionalField.ofValue("foo"));
        assertNotEquals(OptionalField.ofValue("foo"), OptionalField.ofValue("bar"));
        assertNotEquals(OptionalField.ofValue("foo"), OptionalField.ofNothing());
        assertNotEquals(OptionalField.ofNothing(), OptionalField.ofValue("foo"));
        assertEquals(OptionalField.ofNothing(), OptionalField.ofNothing());

        assertEquals(OptionalField.ofValue("foo").hashCode(), OptionalField.ofValue("foo").hashCode());
        assertEquals(OptionalField.ofNothing().hashCode(), OptionalField.ofNothing().hashCode());

        //noinspection ResultOfMethodCallIgnored -- just checking that it does not crash
        OptionalField.ofNothing().toString();

        assertTrue(OptionalField.ofValue("foo").toString().contains("foo"));
    }

}
