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
 * Produces an empty 200 response when null is returned as a response value.
 */
public class NullResponseFactory implements HttpResponseFactory {
    @Override
    public HttpResponse createHttpResponse(RequestCycle requestCycle, Object value) {
        if (value == null) {
            return new StatusOnlyResponse(200);
        } else {
            return null;
        }
    }
}
