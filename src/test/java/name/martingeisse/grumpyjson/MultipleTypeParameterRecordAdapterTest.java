/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import name.martingeisse.grumpyjson.builtin.IntegerAdapter;
import name.martingeisse.grumpyjson.builtin.StringAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.buildCustomObject;
import static name.martingeisse.grumpyjson.JsonTestUtil.createRegistry;

/**
 * This test ensures that handling multiple type parameters works. We also swap their order.
 */
public class MultipleTypeParameterRecordAdapterTest {

    private record Inner<A, B>(A a, B b) {}
    private record Middle<P, Q>(Inner<Q, P> inner) {}
    private record Outer(Middle<Integer, String> middle) {}

    private final JsonRegistry registry = createRegistry(new IntegerAdapter(), new StringAdapter());
    private final JsonTypeAdapter<Outer> outerAdapter = registry.getTypeAdapter(Outer.class);

    @Test
    public void testHappyCase() throws Exception {
        JsonObject innerJson = buildCustomObject("a", new JsonPrimitive("foo"), "b", new JsonPrimitive(12));
        JsonObject middleJson = buildCustomObject("inner", innerJson);
        JsonObject outerJson = buildCustomObject("middle", middleJson);

        Inner<String, Integer> innerRecord = new Inner<>("foo", 12);
        Middle<Integer, String> middleRecord = new Middle<>(innerRecord);
        Outer outerRecord = new Outer(middleRecord);

        Assertions.assertEquals(outerRecord, outerAdapter.fromJson(outerJson, Outer.class));
        Assertions.assertEquals(outerJson, outerAdapter.toJson(outerRecord, Record.class));
    }

}
