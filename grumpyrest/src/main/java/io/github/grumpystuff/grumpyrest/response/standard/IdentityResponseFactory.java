/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest.response.standard;

import io.github.grumpystuff.grumpyrest.RequestCycle;
import io.github.grumpystuff.grumpyrest.response.Response;
import io.github.grumpystuff.grumpyrest.response.ResponseFactory;

import java.util.Objects;

/**
 * This simply accepts response values that implement {@link Response} themselves and returns them unchanged. Without
 * this factory, a handler could not return such a response object.
 */
public final class IdentityResponseFactory implements ResponseFactory {

    /**
     * Constructor.
     */
    public IdentityResponseFactory() {
    }

    @Override
    public Response createResponse(RequestCycle requestCycle, Object value) {
        Objects.requireNonNull(requestCycle, "requestCycle");

        return (value instanceof Response response) ? response : null;
    }

}
