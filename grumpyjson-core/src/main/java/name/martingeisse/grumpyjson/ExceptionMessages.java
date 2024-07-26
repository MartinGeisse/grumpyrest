/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

/**
 * This class is mostly useful for unit tests: If we hardcoded the error messages at the point these errors occur, then
 * we would have to duplicate them in the tests.
 */
public class ExceptionMessages {

    private ExceptionMessages() {
    }

    /**
     * Generic error message for internal errors that we don't want to expose any details about.
     */
    public static final String INTERNAL_ERROR = "internal error";

    /**
     * Error message for an unexpected property in a JSON object.
     */
    public static final String UNEXPECTED_PROPERTY = "unexpected property";

    /**
     * Error message for an missing property in a JSON object.
     */
    public static final String MISSING_PROPERTY = "missing property";

}
