package name.martingeisse.grumpyjson.json_model;

import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonArrayTest {

    @Test
    public void testFactoryMethods() {
        assertInstanceOf(JsonArray.class, JsonArray.of());
        assertInstanceOf(JsonArray.class, JsonArray.of(JsonString.of("foobar")));
        assertInstanceOf(JsonArray.class, JsonArray.of(List.of(JsonString.of("foobar"))));
    }

    @Test
    public void testDeserializationHelpers() throws Exception {
        JsonArray sample = JsonArray.of(JsonString.of("foobar"), JsonNumber.of(42), JsonBoolean.of(true));

        assertThrows(JsonDeserializationException.class, sample::deserializerExpectsNull);
        assertThrows(JsonDeserializationException.class, sample::deserializerExpectsBoolean);
        assertThrows(JsonDeserializationException.class, sample::deserializerExpectsNumber);
        assertThrows(JsonDeserializationException.class, sample::deserializerExpectsString);
        assertThrows(JsonDeserializationException.class, sample::deserializerExpectsObject);

        var elements1 = sample.getAsList();
        var elements2 = sample.deserializerExpectsArray();
        assertEquals(elements1, elements2);

        assertEquals(3, elements1.size());
        assertEquals("foobar", elements1.get(0).deserializerExpectsString());
        assertEquals(42, elements1.get(1).deserializerExpectsNumber().intValue());
        assertTrue(elements1.get(2).deserializerExpectsBoolean());
    }

    @SuppressWarnings({"EqualsWithItself"})
    @Test
    public void testEquals() {
        JsonArray sample1 = JsonArray.of(JsonString.of("foobar"), JsonNumber.of(42), JsonBoolean.of(true));
        JsonArray sample2 = JsonArray.of(JsonString.of("foobar"), JsonNumber.of(42), JsonBoolean.of(true));
        JsonArray sample3 = JsonArray.of(JsonString.of("foobar"), JsonNumber.of(43), JsonBoolean.of(true));
        assertEquals(sample1, sample1);
        assertEquals(sample1, sample2);
        assertNotEquals(sample1, sample3);
    }

    @Test
    public void testHashCode() {
        JsonArray sample1 = JsonArray.of(JsonString.of("foobar"), JsonNumber.of(42), JsonBoolean.of(true));
        JsonArray sample2 = JsonArray.of(JsonString.of("foobar"), JsonNumber.of(42), JsonBoolean.of(true));
        assertEquals(sample1.hashCode(), sample2.hashCode());
    }

}
