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

/**
 * This simply accepts response values that implement {@link Response} themselves.
 */
public final class IdentityResponseFactory implements ResponseFactory {

    @Override
    public Response createResponse(RequestCycle requestCycle, Object value) {
        return (value instanceof Response response) ? response : null;
    }

}
