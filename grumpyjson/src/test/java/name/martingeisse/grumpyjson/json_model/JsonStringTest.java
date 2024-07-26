package name.martingeisse.grumpyjson.json_model;

import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonStringTest {

    @Test
    public void testFactoryMethods() {
        assertInstanceOf(JsonString.class, JsonString.of(""));
        assertInstanceOf(JsonString.class, JsonString.of("foobar"));
    }

    @Test
    public void testDeserializationHelpers() {
        JsonString sample = JsonString.of("foobar");

        assertThrows(JsonDeserializationException.class, sample::deserializerExpectsNull);
        assertThrows(JsonDeserializationException.class, sample::deserializerExpectsBoolean);
        assertThrows(JsonDeserializationException.class, sample::deserializerExpectsNumber);
        assertThrows(JsonDeserializationException.class, sample::deserializerExpectsArray);
        assertThrows(JsonDeserializationException.class, sample::deserializerExpectsObject);

        assertEquals("foobar", sample.getValue());
        assertEquals("foobar", sample.deserializerExpectsString());
        assertEquals("", JsonString.of("").deserializerExpectsString());
    }

    @SuppressWarnings({"StringOperationCanBeSimplified", "EqualsWithItself"})
    @Test
    public void testEquals() {
        JsonString sample1 = JsonString.of("foo");
        JsonString sample2 = JsonString.of(new String("foo"));
        JsonString sample3 = JsonString.of("bar");

        assertEquals(sample1, sample1);
        assertEquals(sample1, sample2);
        assertNotEquals(sample1, sample3);
    }

    @SuppressWarnings("StringOperationCanBeSimplified")
    @Test
    public void testHashCode() {
        JsonString sample1 = JsonString.of("foo");
        JsonString sample2 = JsonString.of(new String("foo"));
        assertEquals(sample1.hashCode(), sample2.hashCode());
    }

}
