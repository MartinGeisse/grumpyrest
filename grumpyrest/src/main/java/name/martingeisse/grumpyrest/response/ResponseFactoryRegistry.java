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
 * Allows to register {@link ResponseFactory}s that provide {@link Response} implementations for other types
 * of response values.
 */
public class ResponseFactoryRegistry {

    private final List<ResponseFactory> factories = new ArrayList<>();

    public ResponseFactoryRegistry() {
    }

    public void clear() {
        factories.clear();
    }

    public void add(ResponseFactory factory) {
        factories.add(factory);
    }

    public Response createResponse(RequestCycle requestCycle, Object value) {
        while (value instanceof ResponseValueWrapper wrapper) {
            value = wrapper.getWrappedResponseValue();
        }
        for (ResponseFactory factory : factories) {
            Response response = factory.createResponse(requestCycle, value);
            if (response != null) {
                return response;
            }
        }
        throw new RuntimeException("no ResponseFactory found for value: " + value);
    }

}
