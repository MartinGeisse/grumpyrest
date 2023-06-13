/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.responder;

import name.martingeisse.grumpyrest.RequestCycle;
import name.martingeisse.grumpyrest.ResponseTransmitter;

import java.io.IOException;

public interface Responder {

    /**
     * Writes a response.
     *
     * Note: If the implementation has to access other things from the {@link RequestCycle}, then these have to be
     * passed to the implementation by another mechanism. This interface tries to keep the dependency between the
     * two minimal.
     */
    void respond(ResponseTransmitter responseTransmitter) throws IOException;

}
