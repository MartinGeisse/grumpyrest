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

import java.time.LocalTime;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;

public class LocalTimeConverterTest {

    private final LocalTimeConverter converter = new LocalTimeConverter();

    @Test
    public void testDeserializationHappyCase() throws Exception {
        Assertions.assertEquals(LocalTime.of(14, 15, 16),
                converter.deserialize(new JsonPrimitive("14:15:16"), LocalTime.class));
        Assertions.assertEquals(LocalTime.of(4, 5, 6),
                converter.deserialize(new JsonPrimitive("04:05:06"), LocalTime.class));
    }

    @Test
    public void testDeserializationWrongFormat() {
        assertFailsDeserialization(converter, new JsonPrimitive("1:2:3"), LocalTime.class);
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
        Assertions.assertEquals(new JsonPrimitive("14:00"),
                converter.serialize(LocalTime.of(14, 0)));
        Assertions.assertEquals(new JsonPrimitive("14:15"),
                converter.serialize(LocalTime.of(14, 15)));
        Assertions.assertEquals(new JsonPrimitive("14:15:16"),
                converter.serialize(LocalTime.of(14, 15, 16)));
        Assertions.assertEquals(new JsonPrimitive("14:15:16.123456789"),
                converter.serialize(LocalTime.of(14, 15, 16, 123456789)));
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(converter, null);
    }

}
