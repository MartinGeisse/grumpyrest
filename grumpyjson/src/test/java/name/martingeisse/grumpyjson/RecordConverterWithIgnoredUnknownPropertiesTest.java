/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import com.google.gson.JsonObject;
import name.martingeisse.grumpyjson.builtin.IntegerConverter;
import name.martingeisse.grumpyjson.builtin.StringConverter;
import name.martingeisse.grumpyjson.builtin.record.RecordConverter;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.assertFailsDeserialization;
import static name.martingeisse.grumpyjson.JsonTestUtil.createRegistries;

public class RecordConverterWithIgnoredUnknownPropertiesTest {

    private record Record(int myInt, String myString) {}

    private final JsonDeserializer deserializer;

    public RecordConverterWithIgnoredUnknownPropertiesTest() {
        JsonRegistries registries = createRegistries(new IntegerConverter(), new StringConverter());
        registries.seal();
        deserializer = new RecordConverter<>(Record.class, registries, new RecordConverter.Options(true));
    }

    @Test
    public void testMissingProperty() {
        JsonObject json = new JsonObject();
        json.addProperty("myInt", 123);
        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(deserializer, json, Record.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "myString")
        );
    }

    @Test
    public void testUnexpectedProperty() throws Exception {
        JsonObject json = new JsonObject();
        json.addProperty("myInt", 123);
        json.addProperty("thisDoesNotBelongHere", 456);
        json.addProperty("myString", "foo");

        Record record = new Record(123, "foo");

        Assertions.assertEquals(record, deserializer.deserialize(json, Record.class));
    }

    @Test
    public void testMissingAndUnexpectedProperty() {
        JsonObject json = new JsonObject();
        json.addProperty("myInt", 123);
        json.addProperty("thisDoesNotBelongHere", 456);
        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(deserializer, json, Record.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "myString")
        );
    }

}
