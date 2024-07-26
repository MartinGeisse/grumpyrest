/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson;

import io.github.grumpystuff.grumpyjson.builtin.IntegerConverter;
import io.github.grumpystuff.grumpyjson.builtin.ListConverter;
import io.github.grumpystuff.grumpyjson.builtin.StringConverter;
import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializer;
import io.github.grumpystuff.grumpyjson.json_model.JsonArray;
import io.github.grumpystuff.grumpyjson.json_model.JsonObject;
import io.github.grumpystuff.grumpyjson.json_model.JsonString;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.grumpystuff.grumpyjson.JsonTestUtil.buildStringArray;
import static io.github.grumpystuff.grumpyjson.JsonTestUtil.createRegistries;

/**
 * This actually tests three things:
 * - generic records: a record that has a type parameter and uses it for a field
 * - type-passing records: a record that has a type parameter and passes it on as a type argument
 *   to another generic type
 * - type-taking record-typed fields: A field whose type is a generic record and which passes a type variable
 *   for the record's type parameter
 */
public class TypePassingRecordConverterTest {

    private record Inner<T>(T best, List<T> others) {}
    private record Middle<T>(Inner<T> inner) {}
    private record Outer(Middle<String> middle) {}

    private final JsonSerializer<Outer> serializer;
    private final JsonDeserializer deserializer;

    public TypePassingRecordConverterTest() throws Exception {
        JsonRegistries registries = createRegistries(new IntegerConverter(), new StringConverter());
        registries.registerDualConverter(new ListConverter(registries));
        registries.seal();
        serializer = registries.getSerializer(Outer.class);
        deserializer = registries.getDeserializer(Outer.class);
    }

    @Test
    public void testHappyCase() throws Exception {
        JsonArray othersJson = buildStringArray("bar", "baz");
        JsonObject innerJson = JsonObject.of("best", JsonString.of("foo"), "others", othersJson);
        JsonObject middleJson = JsonObject.of("inner", innerJson);
        JsonObject outerJson = JsonObject.of("middle", middleJson);

        List<String> otherStrings = List.of("bar", "baz");
        Inner<String> innerRecord = new Inner<>("foo", otherStrings);
        Middle<String> middleRecord = new Middle<>(innerRecord);
        Outer outerRecord = new Outer(middleRecord);

        Assertions.assertEquals(outerRecord, deserializer.deserialize(outerJson, Outer.class));
        Assertions.assertEquals(outerJson, serializer.serialize(outerRecord));
    }

}
