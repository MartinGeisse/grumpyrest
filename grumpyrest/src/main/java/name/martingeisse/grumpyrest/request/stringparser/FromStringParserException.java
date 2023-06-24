/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.stringparser;

/**
 * This exception gets thrown by a {@link FromStringParser} if the string to parse does not conform to the parser's
 * expectation.
 */
public class FromStringParserException extends Exception {

    /**
     * Constructor
     *
     * @param message the exception message
     */
    public FromStringParserException(String message) {
        super(message);
    }

}
