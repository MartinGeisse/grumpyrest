/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonEngineTest {

    private final JsonEngine engine = new JsonEngine();

    @Test
    public void testNullToJson() {
        Assertions.assertThrows(NullPointerException.class, () -> engine.stringify(null));
        Assertions.assertThrows(NullPointerException.class, () -> engine.stringify(null, String.class));
        Assertions.assertThrows(NullPointerException.class, () -> engine.stringify(null, new TypeToken<String>() {}));
    }
}
