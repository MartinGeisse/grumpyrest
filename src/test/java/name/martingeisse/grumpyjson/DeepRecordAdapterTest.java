package name.martingeisse.grumpyjson;

import com.google.gson.JsonObject;
import name.martingeisse.grumpyjson.builtin.IntegerAdapter;
import name.martingeisse.grumpyjson.builtin.StringAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;

public class DeepRecordAdapterTest {

    private record Inner(int myInt, String myString) {}
    private record Outer(Inner inner, int anotherInt) {}

    private final JsonRegistry registry = createRegistry(new IntegerAdapter(), new StringAdapter());
    private final JsonTypeAdapter<Outer> outerAdapter = registry.getTypeAdapter(Outer.class);

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

        Assertions.assertEquals(outerRecord, outerAdapter.fromJson(outerJson, Outer.class));
        Assertions.assertEquals(outerJson, outerAdapter.toJson(outerRecord, Record.class));
    }

    @Test
    public void testMissingPropertyInInner() {
        JsonObject innerJson = new JsonObject();
        innerJson.addProperty("myInt", 123);
        JsonObject outerJson = new JsonObject();
        outerJson.add("inner", innerJson);
        outerJson.addProperty("anotherInt", 456);

        JsonTestUtil.assertFieldErrors(
                assertFailsValidation(outerAdapter, outerJson, Outer.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "inner", "myString")
        );
    }

    @Test
    public void testMissingInnerInOuter() {
        JsonObject outerJson = new JsonObject();
        outerJson.addProperty("anotherInt", 456);

        JsonTestUtil.assertFieldErrors(
                assertFailsValidation(outerAdapter, outerJson, Outer.class),
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
                assertFailsValidation(outerAdapter, outerJson, Outer.class),
                new FieldErrorNode.FlattenedError(ExceptionMessages.MISSING_PROPERTY, "anotherInt")
        );
    }

    @Test
    public void testInnerInOuterHasWrongType() {
        JsonObject outerJson = new JsonObject();
        outerJson.addProperty("inner", "foo");
        outerJson.addProperty("anotherInt", 456);

        JsonTestUtil.assertFieldErrors(
                assertFailsValidation(outerAdapter, outerJson, Outer.class),
                new FieldErrorNode.FlattenedError("expected object, found: \"foo\"", "inner")
        );
    }

}
