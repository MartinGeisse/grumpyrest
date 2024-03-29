/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import name.martingeisse.grumpyjson.builtin.IntegerConverter;
import name.martingeisse.grumpyjson.builtin.StringConverter;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.buildCustomObject;
import static name.martingeisse.grumpyjson.JsonTestUtil.createRegistries;

/**
 * This test ensures that handling multiple type parameters works. We also swap their order.
 */
public class MultipleTypeParameterRecordConverterTest {

    private record Inner<A, B>(A a, B b) {}
    private record Middle<P, Q>(Inner<Q, P> inner) {}
    private record Outer(Middle<Integer, String> middle) {}

    private final JsonSerializer<Outer> serializer;
    private final JsonDeserializer deserializer;

    public MultipleTypeParameterRecordConverterTest() throws Exception {
        JsonRegistries registries = createRegistries(new IntegerConverter(), new StringConverter());
        registries.seal();
        serializer = registries.getSerializer(Outer.class);
        deserializer = registries.getDeserializer(Outer.class);
    }

    @Test
    public void testHappyCase() throws Exception {
        JsonObject innerJson = buildCustomObject("a", new JsonPrimitive("foo"), "b", new JsonPrimitive(12));
        JsonObject middleJson = buildCustomObject("inner", innerJson);
        JsonObject outerJson = buildCustomObject("middle", middleJson);

        Inner<String, Integer> innerRecord = new Inner<>("foo", 12);
        Middle<Integer, String> middleRecord = new Middle<>(innerRecord);
        Outer outerRecord = new Outer(middleRecord);

        Assertions.assertEquals(outerRecord, deserializer.deserialize(outerJson, Outer.class));
        Assertions.assertEquals(outerJson, serializer.serialize(outerRecord));
    }

}
