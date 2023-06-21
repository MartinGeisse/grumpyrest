/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.stringparser;

import name.martingeisse.grumpyrest.ExceptionMessages;

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

    /**
     * Parses value from an absent string. This can be used to return a default for optional parameters.
     * <p>
     * The standard implementation of this method is that missing values are not tolerated, and throws an exception.
     */
    default Object parseFromAbsentString(Type type) throws FromStringParserException {
        throw new FromStringParserException(ExceptionMessages.MISSING_PARAMETER);
    }

}
