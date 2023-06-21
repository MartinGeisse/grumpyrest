/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

/**
 * This class is mostly useful for unit tests, so we don't have to check for exceptions while working around
 * unknown error messages -- this way we just know these messages.
 */
public class ExceptionMessages {

    private ExceptionMessages() {
    }

    /**
     * Error message for an unexpected property in a JSON object.
     */
    public static final String UNEXPECTED_PROPERTY = "unexpected property";

    /**
     * Error message for an missing property in a JSON object.
     */
    public static final String MISSING_PROPERTY = "missing property";

}
