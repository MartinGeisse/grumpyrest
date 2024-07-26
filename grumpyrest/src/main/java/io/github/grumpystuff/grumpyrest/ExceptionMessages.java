/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest;

/**
 * This class is mostly useful for unit tests: If we hardcoded the error messages at the point these errors occur, then
 * we would have to duplicate them in the tests.
 */
public class ExceptionMessages {

    private ExceptionMessages() {
    }

    /**
     * Error message for a missing parameter, such as in the request path or querystring.
     */
    public static final String MISSING_PARAMETER = "missing parameter";

    /**
     * Error message for an unexpected parameter, such as in the request path or querystring.
     */
    public static final String UNEXPECTED_PARAMETER = "unexpected parameter";

    /**
     * Error message for a duplicate parameter, such as in the request path or querystring.
     */
    public static final String DUPLICATE_PARAMETER = "duplicate parameter";

}
