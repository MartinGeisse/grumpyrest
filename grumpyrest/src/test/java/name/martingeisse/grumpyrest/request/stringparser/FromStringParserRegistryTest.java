/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.stringparser;

import name.martingeisse.grumpyrest.request.stringparser.standard.IntegerFromStringParser;
import name.martingeisse.grumpyrest.request.stringparser.standard.StringFromStringParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FromStringParserRegistryTest {

    @Test
    public void testHappyCase() throws Exception {
        FromStringParserRegistry registry = new FromStringParserRegistry();
        registry.registerParser(new StringFromStringParser());
        registry.registerParser(new IntegerFromStringParser());

        assertTrue(registry.supportsType(Integer.TYPE));
        assertTrue(registry.supportsType(Integer.class));
        assertTrue(registry.supportsType(String.class));
        assertFalse(registry.supportsType(Boolean.class));

        assertEquals(5, registry.parseFromString("5", Integer.TYPE));
        assertEquals(5, registry.parseFromString("5", Integer.class));
        assertEquals(5, registry.parseFromString("+5", Integer.class));
        assertEquals(-5, registry.parseFromString("-5", Integer.class));

        assertThrows(FromStringParserException.class, () -> registry.parseFromString(" 5", Integer.class));
        assertThrows(FromStringParserException.class, () -> registry.parseFromString("5 ", Integer.class));
        assertThrows(FromStringParserException.class, () -> registry.parseFromString("5a", Integer.class));
        assertThrows(FromStringParserException.class, () -> registry.parseFromString("a5", Integer.class));

        assertEquals("", registry.parseFromString("", String.class));
        assertEquals("abc", registry.parseFromString("abc", String.class));
        assertEquals("5", registry.parseFromString("5", String.class));
        assertEquals("+5", registry.parseFromString("+5", String.class));
        assertEquals("-5", registry.parseFromString("-5", String.class));
    }

    @Test
    public void testStringNotRegisteredCase() throws Exception {
        FromStringParserRegistry registry = new FromStringParserRegistry();
        registry.registerParser(new IntegerFromStringParser());

        assertTrue(registry.supportsType(Integer.TYPE));
        assertTrue(registry.supportsType(Integer.class));
        assertFalse(registry.supportsType(String.class));
        assertFalse(registry.supportsType(Boolean.class));

        assertEquals(5, registry.parseFromString("5", Integer.TYPE));
        assertEquals(5, registry.parseFromString("5", Integer.class));
        assertEquals(5, registry.parseFromString("+5", Integer.class));
        assertEquals(-5, registry.parseFromString("-5", Integer.class));

        assertThrows(FromStringParserException.class, () -> registry.parseFromString(" 5", Integer.class));
        assertThrows(FromStringParserException.class, () -> registry.parseFromString("5 ", Integer.class));
        assertThrows(FromStringParserException.class, () -> registry.parseFromString("5a", Integer.class));
        assertThrows(FromStringParserException.class, () -> registry.parseFromString("a5", Integer.class));

        assertThrows(FromStringParserException.class, () -> registry.parseFromString("", String.class));
        assertThrows(FromStringParserException.class, () -> registry.parseFromString("abc", String.class));
        assertThrows(FromStringParserException.class, () -> registry.parseFromString("5", String.class));
        assertThrows(FromStringParserException.class, () -> registry.parseFromString("+5", String.class));
        assertThrows(FromStringParserException.class, () -> registry.parseFromString("-5", String.class));
    }

}
