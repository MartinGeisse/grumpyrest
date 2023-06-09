package name.martingeisse.grumpyjson;

import com.google.gson.JsonObject;
import name.martingeisse.grumpyjson.builtin.IntegerAdapter;
import name.martingeisse.grumpyjson.builtin.StringAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;

public class ShallowRecordAdapterTest {

    private record Record(int y, String s) {}

    private final JsonRegistry registry = createRegistry(new IntegerAdapter(), new StringAdapter());
    private final JsonTypeAdapter<Record> adapter = registry.getTypeAdapter(Record.class);

    @Test
    public void testHappyCase() throws Exception {
        JsonObject json = new JsonObject();
        json.addProperty("y", 123);
        json.addProperty("s", "foo");

        Record record = new Record(123, "foo");

        Assertions.assertEquals(record, adapter.fromJson(json, Record.class));
        Assertions.assertEquals(json, adapter.toJson(record, Record.class));
    }

    @Test
    public void testValidationWrongType() {
        forNull(json -> assertFailsValidation(adapter, json, Record.class));
        forNumbers(json -> assertFailsValidation(adapter, json, Record.class));
        forBooleans(json -> assertFailsValidation(adapter, json, Record.class));
        forStrings(json -> assertFailsValidation(adapter, json, Record.class));
        forArrays(json -> assertFailsValidation(adapter, json, Record.class));
    }

    @Test
    public void testGenerationWithNull() {
        assertFailsGenerationWithNpe(adapter, null, String.class);
    }

}
