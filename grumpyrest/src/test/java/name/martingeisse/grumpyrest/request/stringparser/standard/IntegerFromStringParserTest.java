/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.stringparser.standard;

import name.martingeisse.grumpyrest.request.stringparser.FromStringParserException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IntegerFromStringParserTest {

    private static final IntegerFromStringParser parser = new IntegerFromStringParser();

    @Test
    public void test() throws Exception {
        assertTrue(parser.supportsType(Integer.TYPE));
        assertTrue(parser.supportsType(Integer.class));
        assertFalse(parser.supportsType(String.class));

        assertEquals(5, parser.parseFromString("5", Integer.TYPE));
        assertEquals(5, parser.parseFromString("5", Integer.class));
        assertEquals(5, parser.parseFromString("+5", Integer.class));
        assertEquals(-5, parser.parseFromString("-5", Integer.class));

        assertThrows(FromStringParserException.class, () -> parser.parseFromString(" 5", Integer.class));
        assertThrows(FromStringParserException.class, () -> parser.parseFromString("5 ", Integer.class));
        assertThrows(FromStringParserException.class, () -> parser.parseFromString("5a", Integer.class));
        assertThrows(FromStringParserException.class, () -> parser.parseFromString("a5", Integer.class));

        assertThrows(FromStringParserException.class, () -> parser.parseFromAbsentString(Integer.class));
    }

}
