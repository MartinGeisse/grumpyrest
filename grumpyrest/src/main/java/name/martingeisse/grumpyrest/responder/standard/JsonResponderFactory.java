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

public final class JsonResponderFactory implements ResponderFactory {

    @Override
    public Responder createResponder(RequestCycle requestCycle, Object value) {
        if (value == null || !requestCycle.getApi().getJsonEngine().supportsType(value.getClass())) {
            return null;
        }
        return createResponderForSupportedValue(value);
    }

    private Responder createResponderForSupportedValue(Object value) {
        return responseTransmitter -> {
            responseTransmitter.setStatus(200);
            responseTransmitter.setContentType("application/json");
            responseTransmitter.writeJson(value);
        };
    }

}
