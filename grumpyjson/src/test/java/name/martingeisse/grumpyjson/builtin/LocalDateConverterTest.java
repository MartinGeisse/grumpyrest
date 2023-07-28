/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;
import static name.martingeisse.grumpyjson.JsonTestUtil.assertFailsDeserialization;

public class LocalDateConverterTest {

    private final LocalDateConverter converter = new LocalDateConverter();

    @Test
    public void testDeserializationHappyCase() throws Exception {
        Assertions.assertEquals(LocalDate.of(2020, 10, 25), converter.deserialize(new JsonPrimitive("2020-10-25"), LocalDate.class));
        Assertions.assertEquals(LocalDate.of(2020, 1, 5), converter.deserialize(new JsonPrimitive("2020-01-05"), LocalDate.class));
    }

    @Test
    public void testDeserializationWrongFormat() {
        assertFailsDeserialization(converter, new JsonPrimitive("2020-1-05"), LocalDate.class);
        assertFailsDeserialization(converter, new JsonPrimitive("2020-01-5"), LocalDate.class);
        assertFailsDeserialization(converter, new JsonPrimitive("20-01-05"), LocalDate.class);
    }

    @Test
    public void testDeserializationWrongType() throws Exception {
        forNonPrimitive(json -> assertFailsDeserialization(converter, json, String.class));
        forNull(json -> assertFailsDeserialization(converter, json, String.class));
        forBooleans(json -> assertFailsDeserialization(converter, json, String.class));
        forNumbers(json -> assertFailsDeserialization(converter, json, String.class));
    }

    @Test
    public void testSerializationHappyCase() {
        Assertions.assertEquals(new JsonPrimitive("2020-01-05"), converter.serialize(LocalDate.of(2020, 1, 5)));
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(converter, null);
    }

}
