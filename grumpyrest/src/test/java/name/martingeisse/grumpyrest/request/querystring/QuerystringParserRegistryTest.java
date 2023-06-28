/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.querystring;

import name.martingeisse.grumpyjson.builtin.helper_types.OptionalField;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParserRegistry;
import name.martingeisse.grumpyrest.request.stringparser.standard.IntegerFromStringParser;
import name.martingeisse.grumpyrest.request.stringparser.standard.OptionalFieldParser;
import name.martingeisse.grumpyrest.request.stringparser.standard.StringFromStringParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class QuerystringParserRegistryTest {

    private static final FromStringParserRegistry fromStringParserRegistry = new FromStringParserRegistry();
    static {
        fromStringParserRegistry.addParser(new StringFromStringParser());
        fromStringParserRegistry.addParser(new IntegerFromStringParser());
        fromStringParserRegistry.addParser(new OptionalFieldParser(fromStringParserRegistry));
    }

    @Test
    public void testRecord() throws Exception {
        record Foo(int x, String y) {}
        QuerystringParserRegistry registry = new QuerystringParserRegistry(fromStringParserRegistry);
        Assertions.assertEquals(
            new Foo(5, "abc"),
            registry.getParser(Foo.class).parse(Map.of("x", "5", "y", "abc"), Foo.class)
        );
        Assertions.assertThrows(
            QuerystringParsingException.class,
            () -> registry.getParser(Foo.class).parse(Map.of("x", "5 ", "y", "abc"), Foo.class)
        );
        Assertions.assertThrows(
            QuerystringParsingException.class,
            () -> registry.getParser(Foo.class).parse(Map.of("x", "5"), Foo.class)
        );
        Assertions.assertThrows(
            QuerystringParsingException.class,
            () -> registry.getParser(Foo.class).parse(Map.of("x", "5", "y", "abc", "z", "zzz"), Foo.class)
        );
    }

    @Test
    public void testRecordWithOptionalField() throws Exception {
        record Foo(int x, OptionalField<String> y) {}
        QuerystringParserRegistry registry = new QuerystringParserRegistry(fromStringParserRegistry);
        Assertions.assertEquals(
            new Foo(5, OptionalField.ofValue("abc")),
            registry.getParser(Foo.class).parse(Map.of("x", "5", "y", "abc"), Foo.class)
        );
        Assertions.assertThrows(
            QuerystringParsingException.class,
            () -> registry.getParser(Foo.class).parse(Map.of("x", "5 ", "y", "abc"), Foo.class)
        );
        Assertions.assertEquals(
            new Foo(5, OptionalField.ofNothing()),
            registry.getParser(Foo.class).parse(Map.of("x", "5"), Foo.class)
        );
        Assertions.assertThrows(
            QuerystringParsingException.class,
            () -> registry.getParser(Foo.class).parse(Map.of("x", "5", "y", "abc", "z", "zzz"), Foo.class)
        );
    }

}
