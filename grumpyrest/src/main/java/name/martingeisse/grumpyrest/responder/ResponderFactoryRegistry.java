/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.responder;

import name.martingeisse.grumpyrest.RequestCycle;
import name.martingeisse.grumpyrest.ResponseValueWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows to register factories that provide {@link Responder} implementations for other types.
 */
public class ResponderFactoryRegistry {

    private final List<ResponderFactory> factories = new ArrayList<>();

    public ResponderFactoryRegistry() {
    }

    public void clear() {
        factories.clear();
    }

    public void add(ResponderFactory factory) {
        factories.add(factory);
    }

    public Responder createResponder(RequestCycle requestCycle, Object value) {
        while (value instanceof ResponseValueWrapper wrapper) {
            value = wrapper.getWrappedResponseValue();
        }
        for (ResponderFactory factory : factories) {
            Responder responder = factory.createResponder(requestCycle, value);
            if (responder != null) {
                return responder;
            }
        }
        throw new RuntimeException("no responder factory found for value: " + value);
    }

}
