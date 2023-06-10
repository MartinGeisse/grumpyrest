/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.stringparser;

import java.lang.reflect.Type;

public interface FromStringParser {

    /**
     * Checks if this adapter supports the specified type.
     */
    boolean supportsType(Type type);

    /**
     * Parses value from a string.
     */
    Object parseFromString(String s, Type type) throws FromStringParserException;

}
