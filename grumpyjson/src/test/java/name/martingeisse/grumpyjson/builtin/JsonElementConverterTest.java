/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.JsonTestUtil;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class JsonElementConverterTest {

    private final JsonElementConverter converter = new JsonElementConverter();

    private void check(JsonElement element) {
        try {
            assertEquals(element, converter.deserialize(element, JsonElement.class));
        } catch (JsonDeserializationException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkNotSame(JsonElement element) {
        try {
            assertNotSame(element, converter.deserialize(element, JsonElement.class));
        } catch (JsonDeserializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test() {
        JsonTestUtil.forJsonElements(element -> check(element));
        JsonTestUtil.forNonPrimitive(element -> checkNotSame(element));
    }

}
