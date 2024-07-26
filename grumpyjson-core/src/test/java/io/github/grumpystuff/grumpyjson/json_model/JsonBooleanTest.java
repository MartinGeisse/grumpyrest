package io.github.grumpystuff.grumpyjson.json_model;

import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonBooleanTest {

    @Test
    public void testFactoryMethods() {
        assertInstanceOf(JsonBoolean.class, JsonBoolean.FALSE);
        assertInstanceOf(JsonBoolean.class, JsonBoolean.TRUE);
        assertInstanceOf(JsonBoolean.class, JsonBoolean.of(false));
        assertInstanceOf(JsonBoolean.class, JsonBoolean.of(true));
    }

    @Test
    public void testDeserializationHelpers() {
        assertThrows(JsonDeserializationException.class, JsonBoolean.FALSE::deserializerExpectsNull);
        assertThrows(JsonDeserializationException.class, JsonBoolean.FALSE::deserializerExpectsNumber);
        assertThrows(JsonDeserializationException.class, JsonBoolean.FALSE::deserializerExpectsString);
        assertThrows(JsonDeserializationException.class, JsonBoolean.FALSE::deserializerExpectsArray);
        assertThrows(JsonDeserializationException.class, JsonBoolean.FALSE::deserializerExpectsObject);

        assertFalse(JsonBoolean.FALSE.deserializerExpectsBoolean());
        assertTrue(JsonBoolean.TRUE.deserializerExpectsBoolean());
    }

    @SuppressWarnings({"EqualsWithItself", "AssertBetweenInconvertibleTypes"})
    @Test
    public void testEquals() {
        assertEquals(JsonBoolean.FALSE, JsonBoolean.FALSE);
        assertNotEquals(JsonBoolean.FALSE, JsonBoolean.TRUE);
        assertNotEquals(JsonBoolean.TRUE, JsonBoolean.FALSE);
        assertEquals(JsonBoolean.TRUE, JsonBoolean.TRUE);

        assertEquals(JsonBoolean.FALSE, JsonBoolean.of(false));
        assertNotEquals(JsonBoolean.FALSE, JsonBoolean.of(true));
        assertNotEquals(JsonBoolean.TRUE, JsonBoolean.of(false));
        assertEquals(JsonBoolean.TRUE, JsonBoolean.of(true));

        assertEquals(JsonBoolean.of(false), JsonBoolean.FALSE);
        assertNotEquals(JsonBoolean.of(false), JsonBoolean.TRUE);
        assertNotEquals(JsonBoolean.of(true), JsonBoolean.FALSE);
        assertEquals(JsonBoolean.of(true), JsonBoolean.TRUE);

        assertEquals(JsonBoolean.of(false), JsonBoolean.of(false));
        assertNotEquals(JsonBoolean.of(false), JsonBoolean.of(true));
        assertNotEquals(JsonBoolean.of(true), JsonBoolean.of(false));
        assertEquals(JsonBoolean.of(true), JsonBoolean.of(true));

        assertNotEquals(JsonBoolean.FALSE, JsonNumber.of(42));
        assertNotEquals(JsonBoolean.TRUE, JsonNumber.of(42));
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void testHashCode() {
        assertEquals(JsonBoolean.FALSE.hashCode(), JsonBoolean.FALSE.hashCode());
        assertEquals(JsonBoolean.FALSE.hashCode(), JsonBoolean.of(false).hashCode());
        assertEquals(JsonBoolean.of(false).hashCode(), JsonBoolean.FALSE.hashCode());
        assertEquals(JsonBoolean.of(false).hashCode(), JsonBoolean.of(false).hashCode());

        assertEquals(JsonBoolean.TRUE.hashCode(), JsonBoolean.TRUE.hashCode());
        assertEquals(JsonBoolean.TRUE.hashCode(), JsonBoolean.of(true).hashCode());
        assertEquals(JsonBoolean.of(true).hashCode(), JsonBoolean.TRUE.hashCode());
        assertEquals(JsonBoolean.of(true).hashCode(), JsonBoolean.of(true).hashCode());
    }

}
