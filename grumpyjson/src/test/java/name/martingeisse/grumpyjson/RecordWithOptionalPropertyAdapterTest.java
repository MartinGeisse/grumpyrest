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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.createRegistry;

public class RecordWithOptionalPropertyAdapterTest {

    private record Record(OptionalField<Integer> myInt) {}

    private final JsonRegistries registries = createRegistry(new IntegerConverter(), new StringConverter());
    {
        registries.register(new OptionalFieldConverter(registries));
    }
    private final JsonTypeAdapter<Record> converter = registries.get(Record.class);


    @Test
    public void testHappyCaseWithAbsentOptionalProperty() throws Exception {
        JsonObject jsonObjectWithoutValue = new JsonObject();

        Record recordWithoutValue = new Record(OptionalField.ofNothing());

        Assertions.assertEquals(recordWithoutValue, converter.deserialize(jsonObjectWithoutValue, Record.class));
        Assertions.assertEquals(jsonObjectWithoutValue, converter.serialize(recordWithoutValue, Record.class));
    }

    @Test
    public void testHappyCaseWithPresentOptionalProperty() throws Exception {
        JsonObject jsonObjectWithValue = new JsonObject();
        jsonObjectWithValue.addProperty("myInt", 123);

        Record recordWithValue = new Record(OptionalField.ofValue(123));

        Assertions.assertEquals(recordWithValue, converter.deserialize(jsonObjectWithValue, Record.class));
        Assertions.assertEquals(jsonObjectWithValue, converter.serialize(recordWithValue, Record.class));
    }

}
