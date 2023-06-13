/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.response.standard;

import name.martingeisse.grumpyrest.response.ResponseTransmitter;
import name.martingeisse.grumpyrest.response.HttpResponse;

/**
 * Does not respond at all. This is meant to handle cases where the handler has already sent a response manually.
 * If it didn't, then the default behavior from the servlet container will take place.
 */
public class NopResponse implements HttpResponse {

    public static final NopResponse INSTANCE = new NopResponse();

    @Override
    public void transmit(ResponseTransmitter responseTransmitter) {
    }

}
