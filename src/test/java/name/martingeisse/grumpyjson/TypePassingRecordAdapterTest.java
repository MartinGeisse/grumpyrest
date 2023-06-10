package name.martingeisse.grumpyjson;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import name.martingeisse.grumpyjson.builtin.ImmutableListAdapter;
import name.martingeisse.grumpyjson.builtin.StringAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;

/**
 * This actually tests two things:
 * - generic records: a record that has a type parameter and uses it for a field
 * - type-passing records: a record that has a type parameter and passes it on as a type argument
 *   to another generic type
 */
public class TypePassingRecordAdapterTest {

    private record Inner<T>(T best, ImmutableList<T> others) {}
    private record Outer(Inner<String> inner) {}

    private final JsonRegistry registry = createRegistry(new StringAdapter());
    {
        registry.addTypeAdapter(new ImmutableListAdapter(registry));
    }
    private final JsonTypeAdapter<Outer> outerAdapter = registry.getTypeAdapter(Outer.class);

    @Test
    public void testHappyCase() throws Exception {
        JsonArray othersJson = buildStringArray("bar", "baz");
        JsonObject innerJson = buildCustomObject("best", new JsonPrimitive("foo"), "others", othersJson);
        JsonObject outerJson = buildCustomObject("inner", innerJson);

        ImmutableList<String> otherStrings = ImmutableList.of("bar", "baz");
        Inner<String> innerRecord = new Inner<>("foo", otherStrings);
        Outer outerRecord = new Outer(innerRecord);

        Assertions.assertEquals(outerRecord, outerAdapter.fromJson(outerJson, Outer.class));
        Assertions.assertEquals(outerJson, outerAdapter.toJson(outerRecord, Record.class));
    }

}
