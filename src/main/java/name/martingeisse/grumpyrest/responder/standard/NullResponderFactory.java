/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.responder.standard;

import name.martingeisse.grumpyrest.RequestCycle;
import name.martingeisse.grumpyrest.responder.Responder;
import name.martingeisse.grumpyrest.responder.ResponderFactory;

/**
 * Produces an empty 200 response when null is returned as a response value.
 */
public class NullResponderFactory implements ResponderFactory {
    @Override
    public Responder createResponder(RequestCycle requestCycle, Object value) {
        if (value == null) {
            return new StatusOnlyResponder(200);
        } else {
            return null;
        }
    }
}
