/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.registry;

/**
 * Thrown if the registrable for an unknown key is fetched from a {@link Registry}.
 */
public class NotRegisteredException extends Exception {

    /**
     * Constructor.
     */
    public NotRegisteredException() {
    }

    /**
     * Constructor.
     *
     * @param message the exception message
     */
    public NotRegisteredException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message the exception message
     * @param cause   the exception that caused this one
     */
    public NotRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param cause   the exception that caused this one
     */
    public NotRegisteredException(Throwable cause) {
        super(cause);
    }

}
