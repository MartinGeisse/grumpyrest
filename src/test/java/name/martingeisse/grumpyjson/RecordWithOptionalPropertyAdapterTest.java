package name.martingeisse.grumpyjson;

import com.google.gson.JsonObject;
import name.martingeisse.grumpyjson.builtin.IntegerAdapter;
import name.martingeisse.grumpyjson.builtin.StringAdapter;
import name.martingeisse.grumpyjson.builtin.json.JsonOptional;
import name.martingeisse.grumpyjson.builtin.json.JsonOptionalAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.createRegistry;

public class RecordWithOptionalPropertyAdapterTest {

    private record Record(JsonOptional<Integer> myInt) {}

    private final JsonRegistry registry = createRegistry(new IntegerAdapter(), new StringAdapter());
    {
        registry.addTypeAdapter(new JsonOptionalAdapter(registry));
    }
    private final JsonTypeAdapter<Record> adapter = registry.getTypeAdapter(Record.class);


    @Test
    public void testHappyCaseWithAbsentOptionalProperty() throws Exception {
        JsonObject jsonObjectWithoutValue = new JsonObject();

        Record recordWithoutValue = new Record(JsonOptional.ofNothing());

        Assertions.assertEquals(recordWithoutValue, adapter.fromJson(jsonObjectWithoutValue, Record.class));
        Assertions.assertEquals(jsonObjectWithoutValue, adapter.toJson(recordWithoutValue, Record.class));
    }

    @Test
    public void testHappyCaseWithPresentOptionalProperty() throws Exception {
        JsonObject jsonObjectWithValue = new JsonObject();
        jsonObjectWithValue.addProperty("myInt", 123);

        Record recordWithValue = new Record(JsonOptional.ofValue(123));

        Assertions.assertEquals(recordWithValue, adapter.fromJson(jsonObjectWithValue, Record.class));
        Assertions.assertEquals(jsonObjectWithValue, adapter.toJson(recordWithValue, Record.class));
    }

}
