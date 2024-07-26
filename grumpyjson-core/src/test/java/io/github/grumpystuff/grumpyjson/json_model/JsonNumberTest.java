package io.github.grumpystuff.grumpyjson.json_model;

import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonNumberTest {

    private final List<JsonNumber> sampleJsonNumbers = List.of(
            JsonNumber.of(42),
            JsonNumber.of(42.0),
            JsonNumber.of(BigInteger.valueOf(42)),
            JsonNumber.of(BigDecimal.valueOf(42)),
            JsonNumber.of(BigDecimal.valueOf(42.0))
    );

    @Test
    public void testFactoryMethods() {
        assertInstanceOf(JsonNumber.class, JsonNumber.of(42));
        assertInstanceOf(JsonNumber.class, JsonNumber.of(42.0));
        assertInstanceOf(JsonNumber.class, JsonNumber.of(new BigDecimal("1234.42")));
    }

    @Test
    public void testDeserializationHelpers() {
        Number number = new BigDecimal("1234.42");
        JsonNumber jsonNumber = JsonNumber.of(number);

        assertThrows(JsonDeserializationException.class, jsonNumber::deserializerExpectsNull);
        assertThrows(JsonDeserializationException.class, jsonNumber::deserializerExpectsBoolean);
        assertThrows(JsonDeserializationException.class, jsonNumber::deserializerExpectsString);
        assertThrows(JsonDeserializationException.class, jsonNumber::deserializerExpectsArray);
        assertThrows(JsonDeserializationException.class, jsonNumber::deserializerExpectsObject);

        // JsonNumber should not do any computation with numbers, so we should get the same object back
        assertSame(number, jsonNumber.getValue());
        assertSame(number, jsonNumber.deserializerExpectsNumber());
    }

    @Test
    public void testEquals() {
        JsonNumber other = JsonNumber.of(43);
        for (JsonNumber sample1 : sampleJsonNumbers) {
            for (JsonNumber sample2 : sampleJsonNumbers) {
                assertEquals(sample1, sample2);
                assertNotEquals(sample1, other);
            }
        }
    }

    @Test
    public void testHashCode() {
        for (JsonNumber sample1 : sampleJsonNumbers) {
            for (JsonNumber sample2 : sampleJsonNumbers) {
                assertEquals(sample1.hashCode(), sample2.hashCode());
            }
        }
    }

}
