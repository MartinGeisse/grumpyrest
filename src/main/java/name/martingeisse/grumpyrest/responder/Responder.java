/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.responder;

import name.martingeisse.grumpyrest.RequestCycle;

import java.io.IOException;

public interface Responder {

    /**
     * Writes a response.
     */
    void respond(RequestCycle requestCycle) throws IOException;

}
