/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.responder.standard;

import name.martingeisse.grumpyrest.RequestCycle;
import name.martingeisse.grumpyrest.responder.Responder;

/**
 * Does not respond at all. This is meant to handle cases where the handler has already sent a response manually.
 * If it didn't, then the default behavior from the servlet container will take place.
 */
public class NopResponder implements Responder {

    public static final NopResponder INSTANCE = new NopResponder();

    @Override
    public void respond(RequestCycle requestCycle) {
    }

}
