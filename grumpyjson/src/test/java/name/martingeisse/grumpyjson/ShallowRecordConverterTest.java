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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;

public class ShallowRecordConverterTest {

    private record Record(int myInt, String myString) {}

    private final JsonRegistries registries = createRegistry(new IntegerConverter(), new StringConverter());
    private final JsonTypeAdapter<Record> adapter = registries.get(Record.class);

    @Test
    public void testHappyCase() throws Exception {
        JsonObject json = new JsonObject();
        json.addProperty("myInt", 123);
        json.addProperty("myString", "foo");

        Record record = new Record(123, "foo");

        Assertions.assertEquals(record, adapter.deserialize(json, Record.class));
        Assertions.assertEquals(json, adapter.serialize(record, Record.class));
    }

    @Test
    public void testDeserializationWrongType() {
        forNull(json -> assertFailsDeserialization(adapter, json, Record.class));
        forNumbers(json -> assertFailsDeserialization(adapter, json, Record.class));
        forBooleans(json -> assertFailsDeserialization(adapter, json, Record.class));
        forStrings(json -> assertFailsDeserialization(adapter, json, Record.class));
        forArrays(json -> assertFailsDeserialization(adapter, json, Record.class));
    }

    @Test
    public void testMissingProperty() {
        JsonObject json = new JsonObject();
        json.addProperty("myInt", 123);
        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(adapter, json, Record.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "myString")
        );
    }

    @Test
    public void testPropertyWithWrongType() {
        JsonObject json = new JsonObject();
        json.addProperty("myInt", "foo");
        json.addProperty("myString", "foo");
        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(adapter, json, Record.class),
                new FieldErrorNode.FlattenedError("expected integer, found: \"foo\"", "myInt")
        );
    }

    @Test
    public void testUnexpectedProperty() {
        JsonObject json = new JsonObject();
        json.addProperty("myInt", 123);
        json.addProperty("thisDoesNotBelongHere", 456);
        json.addProperty("myString", "foo");
        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(adapter, json, Record.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.UNEXPECTED_PROPERTY, "thisDoesNotBelongHere")
        );
    }

    @Test
    public void testMultipleFieldErrors() {
        JsonObject json = new JsonObject();
        json.addProperty("myInt", "foo");
        json.addProperty("thisDoesNotBelongHere", 456);
        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(adapter, json, Record.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.UNEXPECTED_PROPERTY, "thisDoesNotBelongHere"),
                new FieldErrorNode.FlattenedError("expected integer, found: \"foo\"", "myInt"),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "myString")
        );
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(adapter, null, String.class);
    }

}
