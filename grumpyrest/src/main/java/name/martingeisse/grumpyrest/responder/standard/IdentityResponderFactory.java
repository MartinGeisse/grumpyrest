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
 * This simply accepts response values that implement {@link Responder} themselves.
 */
public final class IdentityResponderFactory implements ResponderFactory {

    @Override
    public Responder createResponder(RequestCycle requestCycle, Object value) {
        return (value instanceof Responder responder) ? responder : null;
    }

}
