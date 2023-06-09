package name.martingeisse.grumpyjson.builtin.json;

import com.google.gson.JsonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;

public class JsonMustBeNullAdapterTest {

    private final JsonMustBeNullAdapter adapter = new JsonMustBeNullAdapter();

    @Test
    public void testValidationHappyCase() throws Exception {
        Assertions.assertEquals(JsonMustBeNull.INSTANCE, adapter.fromJson(JsonNull.INSTANCE, JsonMustBeNull.class));
    }

    @Test
    public void testValidationWrongType() {
        forNonPrimitive(json -> assertFailsValidation(adapter, json, Integer.TYPE));
        forBooleans(json -> assertFailsValidation(adapter, json, Integer.TYPE));
        forNumbers(json -> assertFailsValidation(adapter, json, Integer.TYPE));
        forStrings(json -> assertFailsValidation(adapter, json, Integer.TYPE));
    }

    @Test
    public void testGenerationHappyCase() {
        Assertions.assertEquals(JsonNull.INSTANCE, adapter.toJson(JsonMustBeNull.INSTANCE, JsonMustBeNull.class));
    }

    @Test
    public void testGenerationWithNull() {
        assertFailsGenerationWithNpe(adapter, null, JsonMustBeNull.class);
    }

}
