/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest.request.stringparser.standard;

import io.github.grumpystuff.grumpyjson.TypeToken;
import io.github.grumpystuff.grumpyjson.builtin.helper_types.OptionalField;
import io.github.grumpystuff.grumpyrest.request.stringparser.FromStringParserException;
import io.github.grumpystuff.grumpyrest.request.stringparser.FromStringParserRegistry;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalFieldParserTest {

    @Test
    public void test() throws Exception {

        FromStringParserRegistry registry = new FromStringParserRegistry();
        registry.register(new StringFromStringParser());
        registry.register(new IntegerFromStringParser());
        OptionalFieldParser parser = new OptionalFieldParser(registry);
        registry.seal();

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
