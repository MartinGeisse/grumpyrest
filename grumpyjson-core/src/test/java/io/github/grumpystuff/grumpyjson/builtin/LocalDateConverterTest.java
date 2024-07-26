/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.builtin;

import io.github.grumpystuff.grumpyjson.json_model.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.github.grumpystuff.grumpyjson.JsonTestUtil.*;

public class LocalDateConverterTest {

    private final LocalDateConverter converter = new LocalDateConverter();

    @Test
    public void testDeserializationHappyCase() throws Exception {
        Assertions.assertEquals(LocalDate.of(2020, 10, 25), converter.deserialize(JsonString.of("2020-10-25"), LocalDate.class));
        Assertions.assertEquals(LocalDate.of(2020, 1, 5), converter.deserialize(JsonString.of("2020-01-05"), LocalDate.class));
    }

    @Test
    public void testDeserializationWrongFormat() {
        assertFailsDeserialization(converter, JsonString.of("2020-1-05"), LocalDate.class);
        assertFailsDeserialization(converter, JsonString.of("2020-01-5"), LocalDate.class);
        assertFailsDeserialization(converter, JsonString.of("20-01-05"), LocalDate.class);
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
        Assertions.assertEquals(JsonString.of("2020-01-05"), converter.serialize(LocalDate.of(2020, 1, 5)));
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(converter, null);
    }

}
