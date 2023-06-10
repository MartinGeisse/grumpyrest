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
 * This actually tests three things:
 * - generic records: a record that has a type parameter and uses it for a field
 * - type-passing records: a record that has a type parameter and passes it on as a type argument
 *   to another generic type
 * - type-taking record-typed fields: A field whose type is a generic record and which passes a type variable
 *   for the record's type parameter
 */
public class TypePassingRecordAdapterTest {

    private record Inner<T>(T best, ImmutableList<T> others) {}
    private record Middle<T>(Inner<T> inner) {}
    private record Outer(Middle<String> middle) {}

    private final JsonRegistry registry = createRegistry(new StringAdapter());
    {
        registry.addTypeAdapter(new ImmutableListAdapter(registry));
    }
    private final JsonTypeAdapter<Outer> outerAdapter = registry.getTypeAdapter(Outer.class);

    @Test
    public void testHappyCase() throws Exception {
        JsonArray othersJson = buildStringArray("bar", "baz");
        JsonObject innerJson = buildCustomObject("best", new JsonPrimitive("foo"), "others", othersJson);
        JsonObject middleJson = buildCustomObject("inner", innerJson);
        JsonObject outerJson = buildCustomObject("middle", middleJson);

        ImmutableList<String> otherStrings = ImmutableList.of("bar", "baz");
        Inner<String> innerRecord = new Inner<>("foo", otherStrings);
        Middle<String> middleRecord = new Middle<>(innerRecord);
        Outer outerRecord = new Outer(middleRecord);

        Assertions.assertEquals(outerRecord, outerAdapter.fromJson(outerJson, Outer.class));
        Assertions.assertEquals(outerJson, outerAdapter.toJson(outerRecord, Record.class));
    }

}
