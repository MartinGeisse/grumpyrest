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

    public NotRegisteredException() {
    }

    public NotRegisteredException(String message) {
        super(message);
    }

    public NotRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotRegisteredException(Throwable cause) {
        super(cause);
    }

}
