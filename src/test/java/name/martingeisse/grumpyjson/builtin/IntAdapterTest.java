package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyjson.JsonValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;

public class IntAdapterTest {

    private final IntAdapter adapter = new IntAdapter();

    @Test
    public void testValidationHappyCase() throws Exception {
        Assertions.assertEquals(adapter.fromJson(new JsonPrimitive(0), TypeToken.get(Integer.TYPE)), 0);
        Assertions.assertEquals(adapter.fromJson(new JsonPrimitive(123), TypeToken.get(Integer.TYPE)), 123);
        Assertions.assertEquals(adapter.fromJson(new JsonPrimitive(-123), TypeToken.get(Integer.TYPE)), -123);
    }

    @Test
    public void testValidationWrongType() {
        forNonPrimitive(json -> Assertions.assertThrows(JsonValidationException.class,
                () -> adapter.fromJson(json, TypeToken.get(Integer.TYPE))));
        forNull(json -> Assertions.assertThrows(JsonValidationException.class,
                () -> adapter.fromJson(json, TypeToken.get(Integer.TYPE))));
        forBooleans(json -> Assertions.assertThrows(JsonValidationException.class,
                () -> adapter.fromJson(json, TypeToken.get(Integer.TYPE))));
        forStrings(json -> Assertions.assertThrows(JsonValidationException.class,
                () -> adapter.fromJson(json, TypeToken.get(Integer.TYPE))));
    }

    @Test
    public void testValidationFloat() {
        assertFailsValidation(adapter, new JsonPrimitive(12.34), Integer.TYPE);
    }

    @Test
    public void testValidationSmallLong() throws Exception {
        Assertions.assertEquals(adapter.fromJson(new JsonPrimitive(12L), TypeToken.get(Integer.TYPE)), 12);
    }

    @Test
    public void testValidationTooLarge() {
        assertFailsValidation(adapter, new JsonPrimitive(0x80000000L), Integer.TYPE);
    }

    @Test
    public void testGenerationHappyCase() {
        Assertions.assertEquals(new JsonPrimitive(123), adapter.toJson(123, TypeToken.get(Integer.TYPE)));
    }

    @Test
    public void testGenerationWithNull() {
        assertFailsGeneration(adapter, null, TypeToken.get(Integer.TYPE));
    }

}
