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

public final class JsonResponseFactory implements HttpResponseFactory {

    @Override
    public HttpResponse createHttpResponse(RequestCycle requestCycle, Object value) {
        if (value == null || !requestCycle.getApi().getJsonEngine().supportsType(value.getClass())) {
            return null;
        }
        return createResponseForSupportedValue(value);
    }

    private HttpResponse createResponseForSupportedValue(Object value) {
        return responseTransmitter -> {
            responseTransmitter.setStatus(200);
            responseTransmitter.setContentType("application/json");
            responseTransmitter.writeJson(value);
        };
    }

}
