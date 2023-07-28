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

public class EnumParserTest {

    private enum MyEnum {
        FOO_BAR,
        ABC_DEF
    }

    private enum AnotherEnum {
        FOO_BAR,
        ABC_DEF
    }

    private static final EnumParser<MyEnum> parser = new EnumParser<>(MyEnum.class);

    @Test
    public void test() throws Exception {
        assertTrue(parser.supportsType(MyEnum.class));
        assertFalse(parser.supportsType(AnotherEnum.class));
        assertFalse(parser.supportsType(Enum.class));
        assertFalse(parser.supportsType(String.class));

        assertEquals(MyEnum.FOO_BAR, parser.parseFromString("FOO_BAR", MyEnum.class));
        assertEquals(MyEnum.ABC_DEF, parser.parseFromString("ABC_DEF", MyEnum.class));

        assertThrows(FromStringParserException.class, () -> parser.parseFromString(" FOO_BAR", Integer.class));
        assertThrows(FromStringParserException.class, () -> parser.parseFromString("FOO_BAR ", Integer.class));
        assertThrows(FromStringParserException.class, () -> parser.parseFromString("foo_bar", Integer.class));

        assertThrows(FromStringParserException.class, () -> parser.parseFromAbsentString(Integer.class));
    }

}
