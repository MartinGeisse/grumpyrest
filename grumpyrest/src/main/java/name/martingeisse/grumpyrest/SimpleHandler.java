/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest;

import name.martingeisse.grumpyjson.JsonRegistry;
import name.martingeisse.grumpyrest.request.Request;
import name.martingeisse.grumpyrest.response.FinishRequestException;
import name.martingeisse.grumpyrest.response.Response;
import name.martingeisse.grumpyrest.response.ResponseFactory;
import name.martingeisse.grumpyrest.response.ResponseFactoryRegistry;
import name.martingeisse.grumpyrest.response.ResponseValueWrapper;

public interface SimpleHandler {

    /**
     * Handles a request.
     *
     * The request is passed in its low-level form as a {@link Request} object. High-level properties of the request
     * such as path parameters, querystring parameters and the request body (if any) can be obtained from this object
     * by specifying the classes that represent the high-level structure.
     *
     * The handler returns a response value. This can be any value for which a {@link ResponseFactory} has been
     * registered in the {@link ResponseFactoryRegistry} that turns the value into a {@link Response}. By default, this
     * includes any JSON-compatible values (see {@link JsonRegistry}) as well as pre-built {@link Response} instances.
     *
     * A handler is expected to throw exceptions for invalid requests as well as internal errors. Thrown exceptions
     * will be formally treated like returned exceptions, which in turn are treated like other returned values, but
     * only few exception types have a corresponding {@link ResponseFactory}. Most exception types will instead not
     * have an appropriate factory and will threfore result in an opaque 500 response. This is exactly what is expected
     * for internal errors, to avoid leaking internal data to the client.
     *
     * There is one special case to the above rule: Handler code and the methods it calls can throw a
     * {@link FinishRequestException} to stop the current request and send a response immediately. This is typically
     * used for error handling, such as sending a 404 response somewhere in the method that could not find a record
     * in a database. This exception can again contain any value for which a response factory has been registered, but
     * simple error responses are likely to create a {@link Response} object directly. Formally, it is not
     * {@link FinishRequestException} that gets special treatment but rather any exception or returned value that
     * implements {@link ResponseValueWrapper}.
     *
     * @param request the request, in its low-level form as a {@link Request} object
     * @return a response value, typically a JSON-compatible object or a {@link Response}
     * @throws Exception any exception that indicates a faulty request, internal error or other problem. Will be
     * converted to a response exactly as if the handler returns it.
     */
    Object handle(Request request) throws Exception;

}
