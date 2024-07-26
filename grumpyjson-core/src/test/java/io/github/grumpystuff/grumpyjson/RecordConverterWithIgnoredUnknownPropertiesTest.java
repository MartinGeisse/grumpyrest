/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson;

import io.github.grumpystuff.grumpyjson.builtin.IntegerConverter;
import io.github.grumpystuff.grumpyjson.builtin.StringConverter;
import io.github.grumpystuff.grumpyjson.builtin.record.RecordConverter;
import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializer;
import io.github.grumpystuff.grumpyjson.json_model.JsonNumber;
import io.github.grumpystuff.grumpyjson.json_model.JsonObject;
import io.github.grumpystuff.grumpyjson.json_model.JsonString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.github.grumpystuff.grumpyjson.JsonTestUtil.assertFailsDeserialization;

public class RecordConverterWithIgnoredUnknownPropertiesTest {

    private record Record(int myInt, String myString) {}

    private final JsonDeserializer deserializer;

    public RecordConverterWithIgnoredUnknownPropertiesTest() {
        JsonRegistries registries = JsonTestUtil.createRegistries(new IntegerConverter(), new StringConverter());
        registries.seal();
        deserializer = new RecordConverter<>(Record.class, registries, new RecordConverter.Options(true));
    }

    @Test
    public void testMissingProperty() {
        JsonObject json = JsonObject.of("myInt", JsonNumber.of(123));
        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(deserializer, json, Record.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "myString")
        );
    }

    @Test
    public void testUnexpectedProperty() throws Exception {
        JsonObject json = JsonObject.of(
                "myInt", JsonNumber.of(123),
                "thisDoesNotBelongHere", JsonNumber.of(456),
                "myString", JsonString.of("foo")
        );

        Record record = new Record(123, "foo");

        Assertions.assertEquals(record, deserializer.deserialize(json, Record.class));
    }

    @Test
    public void testMissingAndUnexpectedProperty() {
        JsonObject json = JsonObject.of("myInt", JsonNumber.of(123), "thisDoesNotBelongHere", JsonNumber.of(456));
        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(deserializer, json, Record.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "myString")
        );
    }

}
