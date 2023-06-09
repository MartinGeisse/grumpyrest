package name.martingeisse.grumpyjson;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import name.martingeisse.grumpyjson.builtin.ImmutableListAdapter;
import name.martingeisse.grumpyjson.builtin.IntegerAdapter;
import name.martingeisse.grumpyjson.builtin.StringAdapter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

import static name.martingeisse.grumpyjson.JsonTestUtil.buildArray;
import static name.martingeisse.grumpyjson.JsonTestUtil.createRegistry;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListOfRecordsAdapterTest {

    private record Record(int myInt, String myString) {}

    private final TypeToken<ImmutableList<Record>> listOfRecordsTypeToken = new TypeToken<>() {};
    private final Type listOfRecordsType = listOfRecordsTypeToken.getType();

    private final JsonRegistry registry = createRegistry(new IntegerAdapter(), new StringAdapter());

    {
        registry.addTypeAdapter(new ImmutableListAdapter(registry));
    }

    private final JsonTypeAdapter<ImmutableList<Record>> listOfRecordsAdapter =
            registry.getTypeAdapter(listOfRecordsType);

    @Test
    public void testGenerationHappyCase() throws Exception {

        JsonObject object1 = new JsonObject();
        object1.addProperty("myInt", 12);
        object1.addProperty("myString", "foo");

        JsonObject object2 = new JsonObject();
        object2.addProperty("myInt", 34);
        object2.addProperty("myString", "bar");

        Record record1 = new Record(12, "foo");
        Record record2 = new Record(34, "bar");

        assertEquals(buildArray(), listOfRecordsAdapter.toJson(ImmutableList.of(), listOfRecordsType));
        assertEquals(buildArray(object1), listOfRecordsAdapter.toJson(ImmutableList.of(record1), listOfRecordsType));
        assertEquals(buildArray(object1, object2), listOfRecordsAdapter.toJson(ImmutableList.of(record1, record2), listOfRecordsType));

        assertEquals(ImmutableList.of(), listOfRecordsAdapter.fromJson(buildArray(), listOfRecordsType));
        assertEquals(ImmutableList.of(record1), listOfRecordsAdapter.fromJson(buildArray(object1), listOfRecordsType));
        assertEquals(ImmutableList.of(record1, record2), listOfRecordsAdapter.fromJson(buildArray(object1, object2), listOfRecordsType));
    }

}
