package name.martingeisse.grumpyjson.json_model;

import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JsonObjectTest {

    @Test
    public void testFactoryMethods() {
        assertInstanceOf(JsonObject.class, JsonObject.of());
        assertInstanceOf(JsonObject.class, JsonObject.of("one", JsonString.of("foobar")));
        assertInstanceOf(JsonObject.class, JsonObject.of(Map.of("one", JsonString.of("foobar"))));
    }

    @Test
    public void testDeserializationHelpers() throws Exception {
        JsonObject sample = JsonObject.of(
                "one", JsonString.of("foobar"),
                "two", JsonNumber.of(42),
                "three", JsonBoolean.of(true)
        );

        assertThrows(JsonDeserializationException.class, sample::deserializerExpectsNull);
        assertThrows(JsonDeserializationException.class, sample::deserializerExpectsBoolean);
        assertThrows(JsonDeserializationException.class, sample::deserializerExpectsNumber);
        assertThrows(JsonDeserializationException.class, sample::deserializerExpectsString);
        assertThrows(JsonDeserializationException.class, sample::deserializerExpectsArray);

        var properties1 = sample.getAsMap();
        var properties2 = sample.deserializerExpectsObject();
        assertEquals(properties1, properties2);

        assertEquals(3, properties1.size());
        assertEquals("foobar", properties1.get("one").deserializerExpectsString());
        assertEquals(42, properties1.get("two").deserializerExpectsNumber().intValue());
        assertTrue(properties1.get("three").deserializerExpectsBoolean());
    }

    @SuppressWarnings({"EqualsWithItself"})
    @Test
    public void testEquals() {
        JsonObject sample1 = JsonObject.of(
                "one", JsonString.of("foobar"),
                "two", JsonNumber.of(42),
                "three", JsonBoolean.of(true)
        );
        JsonObject sample2 = JsonObject.of(
                "one", JsonString.of("foobar"),
                "two", JsonNumber.of(42),
                "three", JsonBoolean.of(true)
        );
        JsonObject sample3 = JsonObject.of(
                "one", JsonString.of("foobar"),
                "two", JsonNumber.of(43),
                "three", JsonBoolean.of(true)
        );
        JsonObject sample4 = JsonObject.of(
                "one", JsonString.of("foobar"),
                "twooo", JsonNumber.of(42),
                "three", JsonBoolean.of(true)
        );
        assertEquals(sample1, sample1);
        assertEquals(sample1, sample2);
        assertNotEquals(sample1, sample3);
        assertNotEquals(sample1, sample4);
        assertNotEquals(sample3, sample4);
    }

    @Test
    public void testHashCode() {
        JsonObject sample1 = JsonObject.of(
                "one", JsonString.of("foobar"),
                "two", JsonNumber.of(42),
                "three", JsonBoolean.of(true)
        );
        JsonObject sample2 = JsonObject.of(
                "one", JsonString.of("foobar"),
                "two", JsonNumber.of(42),
                "three", JsonBoolean.of(true)
        );
        assertEquals(sample1.hashCode(), sample2.hashCode());
    }

}
