package io.github.grumpystuff.grumpyjson.json_model;

import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonNullTest {

    @Test
    public void testFactoryMethods() {
        assertInstanceOf(JsonNull.class, JsonNull.INSTANCE);
        assertInstanceOf(JsonNull.class, JsonNull.of());
    }

    @Test
    public void testDeserializationHelpers() {
        JsonNull.INSTANCE.deserializerExpectsNull();
        assertThrows(JsonDeserializationException.class, JsonNull.INSTANCE::deserializerExpectsBoolean);
        assertThrows(JsonDeserializationException.class, JsonNull.INSTANCE::deserializerExpectsNumber);
        assertThrows(JsonDeserializationException.class, JsonNull.INSTANCE::deserializerExpectsString);
        assertThrows(JsonDeserializationException.class, JsonNull.INSTANCE::deserializerExpectsArray);
        assertThrows(JsonDeserializationException.class, JsonNull.INSTANCE::deserializerExpectsObject);
    }

    @SuppressWarnings({"EqualsWithItself", "AssertBetweenInconvertibleTypes"})
    @Test
    public void testEquals() {
        assertEquals(JsonNull.INSTANCE, JsonNull.INSTANCE);
        assertEquals(JsonNull.INSTANCE, JsonNull.of());
        assertEquals(JsonNull.of(), JsonNull.INSTANCE);
        assertEquals(JsonNull.of(), JsonNull.of());
        assertNotEquals(JsonNull.INSTANCE, JsonNumber.of(42));
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void testHashCode() {
        assertEquals(JsonNull.INSTANCE.hashCode(), JsonNull.INSTANCE.hashCode());
        assertEquals(JsonNull.INSTANCE.hashCode(), JsonNull.of().hashCode());
        assertEquals(JsonNull.of().hashCode(), JsonNull.INSTANCE.hashCode());
        assertEquals(JsonNull.of().hashCode(), JsonNull.of().hashCode());
    }

}
