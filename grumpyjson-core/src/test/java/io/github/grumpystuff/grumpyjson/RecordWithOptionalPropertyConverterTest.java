/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson;

import io.github.grumpystuff.grumpyjson.builtin.IntegerConverter;
import io.github.grumpystuff.grumpyjson.builtin.StringConverter;
import io.github.grumpystuff.grumpyjson.builtin.helper_types.OptionalField;
import io.github.grumpystuff.grumpyjson.builtin.helper_types.OptionalFieldConverter;
import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializer;
import io.github.grumpystuff.grumpyjson.json_model.JsonNumber;
import io.github.grumpystuff.grumpyjson.json_model.JsonObject;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RecordWithOptionalPropertyConverterTest {

    private record Record(OptionalField<Integer> myInt) {}

    private final JsonSerializer<Record> serializer;
    private final JsonDeserializer deserializer;

    public RecordWithOptionalPropertyConverterTest() throws Exception {
        JsonRegistries registries = JsonTestUtil.createRegistries(new IntegerConverter(), new StringConverter());
        registries.registerDualConverter(new OptionalFieldConverter(registries));
        registries.seal();
        serializer = registries.getSerializer(Record.class);
        deserializer = registries.getDeserializer(Record.class);
    }

    @Test
    public void testHappyCaseWithAbsentOptionalProperty() throws Exception {
        JsonObject jsonObjectWithoutValue = JsonObject.of();

        Record recordWithoutValue = new Record(OptionalField.ofNothing());

        Assertions.assertEquals(recordWithoutValue, deserializer.deserialize(jsonObjectWithoutValue, Record.class));
        Assertions.assertEquals(jsonObjectWithoutValue, serializer.serialize(recordWithoutValue));
    }

    @Test
    public void testHappyCaseWithPresentOptionalProperty() throws Exception {
        JsonObject jsonObjectWithValue = JsonObject.of("myInt", JsonNumber.of(123));

        Record recordWithValue = new Record(OptionalField.ofValue(123));

        Assertions.assertEquals(recordWithValue, deserializer.deserialize(jsonObjectWithValue, Record.class));
        Assertions.assertEquals(jsonObjectWithValue, serializer.serialize(recordWithValue));
    }

}
