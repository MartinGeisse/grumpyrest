/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest.response.standard;

import io.github.grumpystuff.grumpyrest.response.Response;
import io.github.grumpystuff.grumpyrest.response.ResponseTransmitter;

/**
 * Does not respond at all. This is meant to handle cases where the handler has already sent a response manually.
 * If it didn't, then the default behavior from the servlet container will take place.
 */
public final class NopResponse implements Response {

    /**
     * A shared instance of this class that can be used to reduce memory usage.
     */
    public static final NopResponse INSTANCE = new NopResponse();

    /**
     * Constructor.
     */
    public NopResponse() {
    }

    @Override
    public void transmit(ResponseTransmitter responseTransmitter) {
    }

}
