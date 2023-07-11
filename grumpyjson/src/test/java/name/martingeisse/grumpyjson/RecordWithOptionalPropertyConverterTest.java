/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import com.google.gson.JsonObject;
import name.martingeisse.grumpyjson.builtin.IntegerConverter;
import name.martingeisse.grumpyjson.builtin.StringConverter;
import name.martingeisse.grumpyjson.builtin.helper_types.OptionalField;
import name.martingeisse.grumpyjson.builtin.helper_types.OptionalFieldConverter;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.createRegistries;

public class RecordWithOptionalPropertyConverterTest {

    private record Record(OptionalField<Integer> myInt) {}

    private final JsonSerializer<Record> serializer;
    private final JsonDeserializer deserializer;

    public RecordWithOptionalPropertyConverterTest() throws Exception {
        JsonRegistries registries = createRegistries(new IntegerConverter(), new StringConverter());
        registries.registerDualConverter(new OptionalFieldConverter(registries));
        registries.seal();
        serializer = registries.getSerializer(Record.class);
        deserializer = registries.getDeserializer(Record.class);
    }

    @Test
    public void testHappyCaseWithAbsentOptionalProperty() throws Exception {
        JsonObject jsonObjectWithoutValue = new JsonObject();

        Record recordWithoutValue = new Record(OptionalField.ofNothing());

        Assertions.assertEquals(recordWithoutValue, deserializer.deserialize(jsonObjectWithoutValue, Record.class));
        Assertions.assertEquals(jsonObjectWithoutValue, serializer.serialize(recordWithoutValue));
    }

    @Test
    public void testHappyCaseWithPresentOptionalProperty() throws Exception {
        JsonObject jsonObjectWithValue = new JsonObject();
        jsonObjectWithValue.addProperty("myInt", 123);

        Record recordWithValue = new Record(OptionalField.ofValue(123));

        Assertions.assertEquals(recordWithValue, deserializer.deserialize(jsonObjectWithValue, Record.class));
        Assertions.assertEquals(jsonObjectWithValue, serializer.serialize(recordWithValue));
    }

}
