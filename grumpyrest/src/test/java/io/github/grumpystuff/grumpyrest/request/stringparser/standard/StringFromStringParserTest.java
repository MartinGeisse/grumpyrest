/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest.request.stringparser.standard;

import io.github.grumpystuff.grumpyrest.request.stringparser.FromStringParserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringFromStringParserTest {

    private static final StringFromStringParser parser = new StringFromStringParser();

    @Test
    public void test() {
        assertTrue(parser.supportsType(String.class));
        assertFalse(parser.supportsType(Integer.class));

        assertEquals("", parser.parseFromString("", String.class));
        assertEquals("abc", parser.parseFromString("abc", String.class));
        assertEquals("5", parser.parseFromString("5", String.class));
        assertEquals("+5", parser.parseFromString("+5", String.class));
        assertEquals("-5", parser.parseFromString("-5", String.class));

        assertThrows(FromStringParserException.class, () -> parser.parseFromAbsentString(String.class));
    }

}
