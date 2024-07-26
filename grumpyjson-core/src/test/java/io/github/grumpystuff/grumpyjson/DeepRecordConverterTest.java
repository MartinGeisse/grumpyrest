/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson;

import io.github.grumpystuff.grumpyjson.builtin.IntegerConverter;
import io.github.grumpystuff.grumpyjson.builtin.StringConverter;
import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializer;
import io.github.grumpystuff.grumpyjson.json_model.JsonNumber;
import io.github.grumpystuff.grumpyjson.json_model.JsonObject;
import io.github.grumpystuff.grumpyjson.json_model.JsonString;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.github.grumpystuff.grumpyjson.JsonTestUtil.assertFailsDeserialization;

public class DeepRecordConverterTest {

    private record Inner(int myInt, String myString) {}
    private record Outer(Inner inner, int anotherInt) {}

    private final JsonSerializer<Outer> serializer;
    private final JsonDeserializer deserializer;

    public DeepRecordConverterTest() throws Exception {
        JsonRegistries registries = JsonTestUtil.createRegistries(new IntegerConverter(), new StringConverter());
        registries.seal();
        serializer = registries.getSerializer(Outer.class);
        deserializer = registries.getDeserializer(Outer.class);
    }

    @Test
    public void testHappyCase() throws Exception {
        JsonObject innerJson = JsonObject.of("myInt", JsonNumber.of(123), "myString", JsonString.of("foo"));
        JsonObject outerJson = JsonObject.of("inner", innerJson, "anotherInt", JsonNumber.of(456));

        Inner innerRecord = new Inner(123, "foo");
        Outer outerRecord = new Outer(innerRecord, 456);

        Assertions.assertEquals(outerRecord, deserializer.deserialize(outerJson, Outer.class));
        Assertions.assertEquals(outerJson, serializer.serialize(outerRecord));
    }

    @Test
    public void testMissingPropertyInInner() {
        JsonObject innerJson = JsonObject.of("myInt", JsonNumber.of(123));
        JsonObject outerJson = JsonObject.of("inner", innerJson, "anotherInt", JsonNumber.of(456));

        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(deserializer, outerJson, Outer.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "inner", "myString")
        );
    }

    @Test
    public void testMissingInnerInOuter() {
        JsonObject outerJson = JsonObject.of("anotherInt", JsonNumber.of(456));

        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(deserializer, outerJson, Outer.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "inner")
        );
    }

    @Test
    public void testMissingUnrelatedInOuter() {
        JsonObject innerJson = JsonObject.of("myInt", JsonNumber.of(123), "myString", JsonString.of("foo"));
        JsonObject outerJson = JsonObject.of("inner", innerJson);

        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(deserializer, outerJson, Outer.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "anotherInt")
        );
    }

    @Test
    public void testInnerInOuterHasWrongType() {
        JsonObject outerJson = JsonObject.of("inner", JsonString.of("foo"), "anotherInt", JsonNumber.of(456));

        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(deserializer, outerJson, Outer.class),
                new FieldErrorNode.FlattenedError("expected object, found: JSON:\"foo\"", "inner")
        );
    }

}
