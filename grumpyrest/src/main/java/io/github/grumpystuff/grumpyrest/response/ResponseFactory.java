/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest.response;

import io.github.grumpystuff.grumpyrest.RequestCycle;

/**
 * This factory knows how to turn specific kinds of response values into {@link Response} objects.
 */
public interface ResponseFactory {

    /**
     * Creates a {@link Response} from the specified response value.
     * <p>
     * Each factory only supports a specific set of response values. This method returns null if the response value
     * is not supported, causing the next factory to be tried.
     *
     * @param requestCycle the request cycle to create a response for. This is passed in case the response factory
     *                     wants to do special stuff like look into request properties.
     * @param value        the response value to convert to a {@link Response}.
     *                     May be null if the handler returned null.
     * @return the response or null
     */
    Response createResponse(RequestCycle requestCycle, Object value);

}
