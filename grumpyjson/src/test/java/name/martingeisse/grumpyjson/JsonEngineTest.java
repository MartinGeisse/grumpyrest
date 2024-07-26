/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonEngineTest {

    private final JsonEngine engine = new GsonBasedJsonEngine();

    @Test
    public void testNullToJson() {
        Assertions.assertThrows(NullPointerException.class, () -> engine.serializeToString(null));
    }

}
