/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.stringparser.standard;

import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyjson.builtin.helper_types.OptionalField;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParserException;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParserRegistry;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptionalFieldParserTest {

    @Test
    public void test() throws Exception {

        FromStringParserRegistry registry = new FromStringParserRegistry();
        registry.registerParser(new StringFromStringParser());
        registry.registerParser(new IntegerFromStringParser());
        OptionalFieldParser parser = new OptionalFieldParser(registry);

        Type optionalIntegerType = new TypeToken<OptionalField<Integer>>() {}.getType();

        assertFalse(parser.supportsType(Integer.TYPE));
        assertFalse(parser.supportsType(Integer.class));
        assertFalse(parser.supportsType(String.class));
        assertFalse(parser.supportsType(OptionalField.class));
        assertTrue(parser.supportsType(optionalIntegerType));

        assertEquals(OptionalField.ofValue(5), parser.parseFromString("5", optionalIntegerType));
        assertEquals(OptionalField.ofValue(5), parser.parseFromString("5", optionalIntegerType));
        assertEquals(OptionalField.ofValue(5), parser.parseFromString("+5", optionalIntegerType));
        assertEquals(OptionalField.ofValue(-5), parser.parseFromString("-5", optionalIntegerType));

        assertThrows(FromStringParserException.class, () -> parser.parseFromString(" 5", optionalIntegerType));
        assertThrows(FromStringParserException.class, () -> parser.parseFromString("5 ", optionalIntegerType));
        assertThrows(FromStringParserException.class, () -> parser.parseFromString("5a", optionalIntegerType));
        assertThrows(FromStringParserException.class, () -> parser.parseFromString("a5", optionalIntegerType));

        assertEquals(OptionalField.ofNothing(), parser.parseFromAbsentString(optionalIntegerType));
    }

}
