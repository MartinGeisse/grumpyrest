package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;

public class IntegerAdapterTest {

    private final IntegerAdapter adapter = new IntegerAdapter();

    @Test
    public void testValidationHappyCase() throws Exception {
        Assertions.assertEquals(adapter.fromJson(new JsonPrimitive(0), Integer.TYPE), 0);
        Assertions.assertEquals(adapter.fromJson(new JsonPrimitive(123), Integer.TYPE), 123);
        Assertions.assertEquals(adapter.fromJson(new JsonPrimitive(-123), Integer.TYPE), -123);
    }

    @Test
    public void testValidationWrongType() {
        forNonPrimitive(json -> assertFailsValidation(adapter, json, Integer.TYPE));
        forNull(json -> assertFailsValidation(adapter, json, Integer.TYPE));
        forBooleans(json -> assertFailsValidation(adapter, json, Integer.TYPE));
        forStrings(json -> assertFailsValidation(adapter, json, Integer.TYPE));
    }

    @Test
    public void testValidationFloat() {
        assertFailsValidation(adapter, new JsonPrimitive(12.34), Integer.TYPE);
    }

    @Test
    public void testValidationSmallLong() throws Exception {
        Assertions.assertEquals(adapter.fromJson(new JsonPrimitive(12L), Integer.TYPE), 12);
    }

    @Test
    public void testValidationTooLarge() {
        assertFailsValidation(adapter, new JsonPrimitive(0x80000000L), Integer.TYPE);
    }

    @Test
    public void testGenerationHappyCase() {
        Assertions.assertEquals(new JsonPrimitive(123), adapter.toJson(123, Integer.TYPE));
    }

    @Test
    public void testGenerationWithNull() {
        assertFailsGenerationWithNpe(adapter, null, Integer.TYPE);
    }

}
