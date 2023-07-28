/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static name.martingeisse.grumpyjson.JsonTestUtil.forJsonElements;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IgnoredFieldConverterTest {

    private final IgnoredFieldConverter converter = new IgnoredFieldConverter();

    @Test
    public void testDeserialization() throws Exception {
        forJsonElements(json -> assertEquals(IgnoredField.INSTANCE, converter.deserialize(json, IgnoredField.class)));
        forJsonElements(json -> assertEquals(IgnoredField.INSTANCE, converter.deserializeAbsent(IgnoredField.class)));
    }

    @Test
    public void testSerializationHappyCase() {
        assertEquals(Optional.empty(), converter.serializeOptional(IgnoredField.INSTANCE));
    }

    @Test
    public void testSerializationWithNull() {
        assertThrows(NullPointerException.class, () -> converter.serializeOptional(null));
    }

    @Test
    public void testOnlyWorksInVanishableLocations() {
        assertThrows(JsonSerializationException.class, () -> converter.serialize(IgnoredField.INSTANCE));
    }

}
