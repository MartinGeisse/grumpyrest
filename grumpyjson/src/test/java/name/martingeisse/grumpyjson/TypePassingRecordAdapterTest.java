/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import name.martingeisse.grumpyjson.builtin.ListAdapter;
import name.martingeisse.grumpyjson.builtin.StringAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    private record Inner<T>(T best, List<T> others) {}
    private record Middle<T>(Inner<T> inner) {}
    private record Outer(Middle<String> middle) {}

    private final JsonRegistry registry = createRegistry(new StringAdapter());
    {
        registry.addTypeAdapter(new ListAdapter(registry));
    }
    private final JsonTypeAdapter<Outer> outerAdapter = registry.getTypeAdapter(Outer.class);

    @Test
    public void testHappyCase() throws Exception {
        JsonArray othersJson = buildStringArray("bar", "baz");
        JsonObject innerJson = buildCustomObject("best", new JsonPrimitive("foo"), "others", othersJson);
        JsonObject middleJson = buildCustomObject("inner", innerJson);
        JsonObject outerJson = buildCustomObject("middle", middleJson);

        List<String> otherStrings = List.of("bar", "baz");
        Inner<String> innerRecord = new Inner<>("foo", otherStrings);
        Middle<String> middleRecord = new Middle<>(innerRecord);
        Outer outerRecord = new Outer(middleRecord);

        Assertions.assertEquals(outerRecord, outerAdapter.fromJson(outerJson, Outer.class));
        Assertions.assertEquals(outerJson, outerAdapter.toJson(outerRecord, Record.class));
    }

}
