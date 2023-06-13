/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.response.standard;

import name.martingeisse.grumpyrest.RequestCycle;
import name.martingeisse.grumpyrest.response.HttpResponse;
import name.martingeisse.grumpyrest.response.HttpResponseFactory;

/**
 * This simply accepts response values that implement {@link HttpResponse} themselves.
 */
public final class IdentityResponseFactory implements HttpResponseFactory {

    @Override
    public HttpResponse createHttpResponse(RequestCycle requestCycle, Object value) {
        return (value instanceof HttpResponse httpResponse) ? httpResponse : null;
    }

}
