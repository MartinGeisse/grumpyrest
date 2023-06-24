/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.response;

/**
 * This exception type can be thrown to immediately finish handling a request and respond with a specific response
 * value. This value can be a normal response value, a response using the standard error format, a custom HTTP-level
 * response such as a redirect, or whatever.
 * <p>
 * This class achieves this by bringing two things together: Being an exception for the control-flow part (stop
 * handling the request immediately) and implementing {@link ResponseValueWrapper} to provide an arbitrary value as
 * if it were returned from a handler.
 * <p>
 * The latter causes the framework to select an appropriate {@link ResponseFactory} for that wrapped value. This uses
 * the normal mechanism to handle response values, but will usually be one of two cases. Either the response value is
 * JSON-able (normal response, as well as standard error response) or is a {@link Response} itself (redirect,
 * request for HTTP authentication, or similar). However, you are not limited to these cases, and might want to use
 * the full flexibility of the {@link ResponseFactoryRegistry}.
 */
public class FinishRequestException extends RuntimeException implements ResponseValueWrapper {

    private final Object responseValue;

    /**
     * Constructor.
     *
     * @param responseValue the response value to respond
     */
    public FinishRequestException(Object responseValue) {
        this.responseValue = responseValue;
    }

    @Override
    public Object getWrappedResponseValue() {
        return responseValue;
    }

}
