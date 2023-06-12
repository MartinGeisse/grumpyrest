/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import com.google.gson.JsonObject;
import name.martingeisse.grumpyjson.builtin.IntegerAdapter;
import name.martingeisse.grumpyjson.builtin.StringAdapter;
import name.martingeisse.grumpyjson.builtin.helper_types.OptionalField;
import name.martingeisse.grumpyjson.builtin.helper_types.OptionalFieldAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.createRegistry;

public class RecordWithOptionalPropertyAdapterTest {

    private record Record(OptionalField<Integer> myInt) {}

    private final JsonRegistry registry = createRegistry(new IntegerAdapter(), new StringAdapter());
    {
        registry.addTypeAdapter(new OptionalFieldAdapter(registry));
    }
    private final JsonTypeAdapter<Record> adapter = registry.getTypeAdapter(Record.class);


    @Test
    public void testHappyCaseWithAbsentOptionalProperty() throws Exception {
        JsonObject jsonObjectWithoutValue = new JsonObject();

        Record recordWithoutValue = new Record(OptionalField.ofNothing());

        Assertions.assertEquals(recordWithoutValue, adapter.fromJson(jsonObjectWithoutValue, Record.class));
        Assertions.assertEquals(jsonObjectWithoutValue, adapter.toJson(recordWithoutValue, Record.class));
    }

    @Test
    public void testHappyCaseWithPresentOptionalProperty() throws Exception {
        JsonObject jsonObjectWithValue = new JsonObject();
        jsonObjectWithValue.addProperty("myInt", 123);

        Record recordWithValue = new Record(OptionalField.ofValue(123));

        Assertions.assertEquals(recordWithValue, adapter.fromJson(jsonObjectWithValue, Record.class));
        Assertions.assertEquals(jsonObjectWithValue, adapter.toJson(recordWithValue, Record.class));
    }

}
