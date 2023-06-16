/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.response;

import name.martingeisse.grumpyrest.RequestCycle;

public interface ResponseFactory {

    /**
     * Returns null on failure, causing the next factory to be tried.
     */
    Response createResponse(RequestCycle requestCycle, Object value);

}