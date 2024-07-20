/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.response.standard;

import name.martingeisse.grumpyrest.RequestCycle;
import name.martingeisse.grumpyrest.response.Response;
import name.martingeisse.grumpyrest.response.ResponseFactory;

import java.util.Objects;

/**
 * Produces an empty 200 response when null is returned as a response value.
 */
public final class NullResponseFactory implements ResponseFactory {

    /**
     * Constructor.
     */
    public NullResponseFactory() {
    }

    @Override
    public Response createResponse(RequestCycle requestCycle, Object value) {
        Objects.requireNonNull(requestCycle, "requestCycle");

        if (value == null) {
            return new StatusOnlyResponse(200);
        } else {
            return null;
        }
    }

}
