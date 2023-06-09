package name.martingeisse.grumpyjson.builtin.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonOptionalTest {

    private final JsonOptional<String> ofNothing = JsonOptional.ofNothing();
    private final JsonOptional<String> ofValue = JsonOptional.ofValue("foo");

    @Test
    public void testGetters() {
        assertTrue(ofNothing.isAbsent());
        assertFalse(ofNothing.isPresent());
        assertNull(ofNothing.getValueOrNothingAsNull());
        assertThrows(IllegalStateException.class, () -> ofNothing.getValue());

        assertFalse(ofValue.isAbsent());
        assertTrue(ofValue.isPresent());
        assertEquals("foo", ofValue.getValueOrNothingAsNull());
        assertEquals("foo", ofValue.getValue());
    }

    @Test
    public void testFactoryMethods() {
        assertEquals("foo", JsonOptional.ofValue("foo").getValueOrNothingAsNull());
        assertThrows(NullPointerException.class, () -> JsonOptional.ofValue(null));

        assertNull(JsonOptional.ofNothing().getValueOrNothingAsNull());

        assertEquals("foo", JsonOptional.ofValueOrNullAsNothing("foo").getValueOrNothingAsNull());
        assertNull(JsonOptional.ofValueOrNullAsNothing(null).getValueOrNothingAsNull());
    }

}
