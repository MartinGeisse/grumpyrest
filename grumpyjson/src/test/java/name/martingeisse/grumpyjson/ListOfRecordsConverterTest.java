/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonObject;
import name.martingeisse.grumpyjson.builtin.IntegerConverter;
import name.martingeisse.grumpyjson.builtin.ListConverter;
import name.martingeisse.grumpyjson.builtin.StringConverter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;

import static name.martingeisse.grumpyjson.JsonTestUtil.buildArray;
import static name.martingeisse.grumpyjson.JsonTestUtil.createRegistry;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListOfRecordsConverterTest {

    private record Record(int myInt, String myString) {}

    private final TypeToken<List<Record>> listOfRecordsTypeToken = new TypeToken<>() {};
    private final Type listOfRecordsType = listOfRecordsTypeToken.getType();

    private final JsonRegistries registries = createRegistry(new IntegerConverter(), new StringConverter());

    {
        registries.register(new ListConverter(registries));
    }

    private final JsonTypeAdapter<List<Record>> listOfRecordsAdapter = registries.get(listOfRecordsTypeToken);

    @Test
    public void testSerializationHappyCase() throws Exception {

        JsonObject object1 = new JsonObject();
        object1.addProperty("myInt", 12);
        object1.addProperty("myString", "foo");

        JsonObject object2 = new JsonObject();
        object2.addProperty("myInt", 34);
        object2.addProperty("myString", "bar");

        Record record1 = new Record(12, "foo");
        Record record2 = new Record(34, "bar");

        assertEquals(buildArray(), listOfRecordsAdapter.serialize(List.of(), listOfRecordsType));
        assertEquals(buildArray(object1), listOfRecordsAdapter.serialize(List.of(record1), listOfRecordsType));
        assertEquals(buildArray(object1, object2), listOfRecordsAdapter.serialize(List.of(record1, record2), listOfRecordsType));

        assertEquals(List.of(), listOfRecordsAdapter.deserialize(buildArray(), listOfRecordsType));
        assertEquals(List.of(record1), listOfRecordsAdapter.deserialize(buildArray(object1), listOfRecordsType));
        assertEquals(List.of(record1, record2), listOfRecordsAdapter.deserialize(buildArray(object1, object2), listOfRecordsType));
    }

}
