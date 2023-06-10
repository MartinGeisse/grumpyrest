/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.responder;

import name.martingeisse.grumpyrest.RequestCycle;

public interface ResponderFactory {

    /**
     * Returns null on failure, causing the next factory to be tried.
     */
    Responder createResponder(RequestCycle requestCycle, Object value);

}
