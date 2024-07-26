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

import java.time.LocalDateTime;

import static io.github.grumpystuff.grumpyjson.JsonTestUtil.*;

public class LocalDateTimeConverterTest {

    private final LocalDateTimeConverter converter = new LocalDateTimeConverter();

    @Test
    public void testDeserializationHappyCase() throws Exception {
        Assertions.assertEquals(LocalDateTime.of(2020, 10, 25, 14, 15, 16),
                converter.deserialize(JsonString.of("2020-10-25T14:15:16"), LocalDateTime.class));
        Assertions.assertEquals(LocalDateTime.of(2020, 1, 5, 4, 5, 6),
                converter.deserialize(JsonString.of("2020-01-05T04:05:06"), LocalDateTime.class));
    }

    @Test
    public void testDeserializationWrongFormat() {
        assertFailsDeserialization(converter, JsonString.of("2020-01-05T1:2:3"), LocalDateTime.class);
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
        Assertions.assertEquals(JsonString.of("2020-01-05T14:00"),
                converter.serialize(LocalDateTime.of(2020, 1, 5, 14, 0)));
        Assertions.assertEquals(JsonString.of("2020-01-05T14:15"),
                converter.serialize(LocalDateTime.of(2020, 1, 5, 14, 15)));
        Assertions.assertEquals(JsonString.of("2020-01-05T14:15:16"),
                converter.serialize(LocalDateTime.of(2020, 1, 5, 14, 15, 16)));
        Assertions.assertEquals(JsonString.of("2020-01-05T14:15:16.123456789"),
                converter.serialize(LocalDateTime.of(2020, 1, 5, 14, 15, 16, 123456789)));
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(converter, null);
    }

}
