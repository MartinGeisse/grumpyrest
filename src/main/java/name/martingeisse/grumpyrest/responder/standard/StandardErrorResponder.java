/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.responder.standard;

import com.google.common.collect.ImmutableList;
import name.martingeisse.grumpyrest.RequestCycle;
import name.martingeisse.grumpyrest.responder.Responder;

import java.io.IOException;

public record StandardErrorResponder(int status, String message, ImmutableList<Field> fields) implements Responder {

    /**
     * This gets responded (if even possible) when reading the request failed with a network error.
     */
    public static final StandardErrorResponder IO_ERROR = new StandardErrorResponder(400, "I/O error");

    /**
     * This response indicates that no route is known for the requested URL. It usually indicates a bug in the
     * client, such as a typo in the URL.
     */
    public static final StandardErrorResponder UNKNOWN_URL = new StandardErrorResponder(404, "unknown URL");

    /**
     * This response indicates that the route is known in principle, but contains an ID that does not exist. This can
     * happen routinely when requesting entities that are not known in advance to exist.
     */
    public static final StandardErrorResponder ID_NOT_FOUND = new StandardErrorResponder(404, "ID not found");

    /**
     * Most endpoints expect a JSON request body (if they expect a request body at all), so here is a standard
     * response for other request content types.
     */
    public static final StandardErrorResponder JSON_EXPECTED = new StandardErrorResponder(415, "expected application/json content type");

    /**
     * The standard response for "something went wrong in the server", usually an uncaught exception.
     */
    public static final StandardErrorResponder INTERNAL_SERVER_ERROR = new StandardErrorResponder(500, "internal server error");

    public StandardErrorResponder(int status, String message) {
        this(status, message, ImmutableList.of());
    }

    public StandardErrorResponder(int status, String message, Field field) {
        this(status, message, ImmutableList.of(field));
    }

    @Override
    public void respond(RequestCycle requestCycle) throws IOException {
        var response = requestCycle.getResponse();
        response.setStatus(status);
        response.setContentType("application/json");

        Body body = new Body(message, fields);
        requestCycle.getApi().getJsonEngine().writeTo(body, response.getOutputStream());
    }

    public record Field(String path, String message) {}
    public record Body(String message, ImmutableList<Field> fields) {}
}
