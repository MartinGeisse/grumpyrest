/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.response;

import name.martingeisse.grumpyrest.RequestCycle;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows to register {@link HttpResponseFactory}s that provide {@link HttpResponse} implementations for other types
 * of response values.
 */
public class HttpResponseFactoryRegistry {

    private final List<HttpResponseFactory> factories = new ArrayList<>();

    public HttpResponseFactoryRegistry() {
    }

    public void clear() {
        factories.clear();
    }

    public void add(HttpResponseFactory factory) {
        factories.add(factory);
    }

    public HttpResponse createHttpResponse(RequestCycle requestCycle, Object value) {
        while (value instanceof ResponseValueWrapper wrapper) {
            value = wrapper.getWrappedResponseValue();
        }
        for (HttpResponseFactory factory : factories) {
            HttpResponse httpResponse = factory.createHttpResponse(requestCycle, value);
            if (httpResponse != null) {
                return httpResponse;
            }
        }
        throw new RuntimeException("no HttpResponseFactory found for value: " + value);
    }

}
