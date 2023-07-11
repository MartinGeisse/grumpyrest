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

public class DeepRecordConverterTest {

    private record Inner(int myInt, String myString) {}
    private record Outer(Inner inner, int anotherInt) {}

    private final JsonRegistries registries = createRegistry(new IntegerConverter(), new StringConverter());
    private final JsonTypeAdapter<Outer> outerAdapter = registries.get(Outer.class);

    @Test
    public void testHappyCase() throws Exception {
        JsonObject innerJson = new JsonObject();
        innerJson.addProperty("myInt", 123);
        innerJson.addProperty("myString", "foo");
        JsonObject outerJson = new JsonObject();
        outerJson.add("inner", innerJson);
        outerJson.addProperty("anotherInt", 456);

        Inner innerRecord = new Inner(123, "foo");
        Outer outerRecord = new Outer(innerRecord, 456);

        Assertions.assertEquals(outerRecord, outerAdapter.deserialize(outerJson, Outer.class));
        Assertions.assertEquals(outerJson, outerAdapter.serialize(outerRecord, Record.class));
    }

    @Test
    public void testMissingPropertyInInner() {
        JsonObject innerJson = new JsonObject();
        innerJson.addProperty("myInt", 123);
        JsonObject outerJson = new JsonObject();
        outerJson.add("inner", innerJson);
        outerJson.addProperty("anotherInt", 456);

        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(outerAdapter, outerJson, Outer.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "inner", "myString")
        );
    }

    @Test
    public void testMissingInnerInOuter() {
        JsonObject outerJson = new JsonObject();
        outerJson.addProperty("anotherInt", 456);

        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(outerAdapter, outerJson, Outer.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "inner")
        );
    }

    @Test
    public void testMissingUnrelatedInOuter() {
        JsonObject innerJson = new JsonObject();
        innerJson.addProperty("myInt", 123);
        innerJson.addProperty("myString", "foo");
        JsonObject outerJson = new JsonObject();
        outerJson.add("inner", innerJson);

        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(outerAdapter, outerJson, Outer.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "anotherInt")
        );
    }

    @Test
    public void testInnerInOuterHasWrongType() {
        JsonObject outerJson = new JsonObject();
        outerJson.addProperty("inner", "foo");
        outerJson.addProperty("anotherInt", 456);

        JsonTestUtil.assertFieldErrors(
                assertFailsDeserialization(outerAdapter, outerJson, Outer.class),
                new FieldErrorNode.FlattenedError("expected object, found: \"foo\"", "inner")
        );
    }

}
