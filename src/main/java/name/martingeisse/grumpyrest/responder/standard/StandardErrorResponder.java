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

    public static final StandardErrorResponder UNKNOWN_URL = new StandardErrorResponder(404, "unknown URL");
    public static final StandardErrorResponder INTERNAL_SERVER_ERROR = new StandardErrorResponder(500, "internal server error");
    public static final StandardErrorResponder JSON_EXPECTED = new StandardErrorResponder(415, "expected application/json content type");
    public static final StandardErrorResponder IO_ERROR = new StandardErrorResponder(400, "I/O error");

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
