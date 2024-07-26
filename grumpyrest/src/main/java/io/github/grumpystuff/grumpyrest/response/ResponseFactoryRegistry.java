/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest.response;

import io.github.grumpystuff.grumpyjson.registry.Registry;
import io.github.grumpystuff.grumpyjson.registry.Sealable;
import io.github.grumpystuff.grumpyjson.util.ListUtil;
import io.github.grumpystuff.grumpyrest.RequestCycle;
import io.github.grumpystuff.grumpyrest.RestApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Allows to register {@link ResponseFactory}s that provide {@link Response} implementations for the response values
 * returned by handlers.
 * <p>
 * This class does not extend {@link Registry} because it does not work with keys reported by its registrables (i.e.
 * response factories), but directly calls all factories and uses the first non-null result.
 */
public final class ResponseFactoryRegistry extends Sealable {

    private final List<ResponseFactory> factories = new ArrayList<>();

    /**
     * Constructor. This constructor does not register any factories, but the {@link RestApi} calling this constructor
     * does.
     */
    public ResponseFactoryRegistry() {
    }

    /**
     * Removes all response factories from this registry. This is useful because the registry that is used by a newly
     * created {@link RestApi} contains default response factories, and the code using it might not want to use them.
     */
    public void clear() {
        ensureConfigurationPhase();
        factories.clear();
    }

    /**
     * Registers a response factory to this registry, to be used when converting a response value to a {@link Response}.
     *
     * @param factory the response factory to register
     */
    public void register(ResponseFactory factory) {
        Objects.requireNonNull(factory, "factory");
        ensureConfigurationPhase();

        factories.add(factory);
    }

    @Override
    protected void onSeal() {
        ListUtil.reverseInPlace(factories);
    }

    /**
     * Converts a response value to a {@link Response} using an appropriate response factory from this registry.
     * If multiple response factories support conversion of that value, then the one registered later takes precedence.
     *
     * @param requestCycle the request cycle to create a response for. This is passed in case the response factory
     *                     wants to do special stuff like look into request properties.
     * @param value        the response value to convert to a {@link Response}
     * @return the response
     */
    public Response createResponse(RequestCycle requestCycle, Object value) {
        Objects.requireNonNull(requestCycle, "requestCycle");

        ensureRunTimePhase();
        while (value instanceof ResponseValueWrapper wrapper) {
            value = wrapper.getWrappedResponseValue();
        }
        for (ResponseFactory factory : factories) {
            Response response = factory.createResponse(requestCycle, value);
            if (response != null) {
                return response;
            }
        }
        throw new NoResponseFactoryException(value);
    }

}
