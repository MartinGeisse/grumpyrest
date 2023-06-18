/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest;

import name.martingeisse.grumpyrest.response.ResponseTransmitter;

/**
 * Interface for a handler in case a {@link SimpleHandler} is not flexible enough. A complex Handler takes a
 * {@link RequestCycle}. It can, for example, access the {@link ResponseTransmitter} directly and stream a large
 * binary response without loading it all into memory first.
 * <p>
 * Note that implementing ComplexHandler is not more complex than how a {@link SimpleHandler} works, but rather is
 * needed for more complex cases. The main downside with a complex handler is that a request cycle is much harder to
 * mock in tests than just a {@link Request}. You should therefore implement {@link SimpleHandler} whenever possible
 * to reduce scaffolding for your tests,
 */
public interface ComplexHandler {

    /**
     * Handles a request. The general interface is similar to {@link SimpleHandler}, except that this method gets the
     * whole {@link RequestCycle} insteaf of just the {@link Request}. A complex handler can obtain the request by
     * calling {@link RequestCycle#getHighlevelRequest()}
     *
     * @param requestCycle the request cycle object which contains all objects related to handling a single request
     * @return the response value, just like for {@link SimpleHandler}
     * @throws Exception on internal errors or faulty requests, just like for {@link SimpleHandler}
     */
    Object handle(RequestCycle requestCycle) throws Exception;

}
