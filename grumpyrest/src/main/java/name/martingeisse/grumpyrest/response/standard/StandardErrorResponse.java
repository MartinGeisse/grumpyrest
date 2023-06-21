/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.response.standard;

import name.martingeisse.grumpyjson.FieldErrorNode;
import name.martingeisse.grumpyjson.JsonValidationException;
import name.martingeisse.grumpyrest.response.Response;
import name.martingeisse.grumpyrest.response.ResponseTransmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record StandardErrorResponse(int status, String message, List<Field> fields) implements Response {

    public StandardErrorResponse {
        fields = List.copyOf(fields);
    }

    /**
     * This gets responded (if even possible) when reading the request failed with a network error.
     */
    public static final StandardErrorResponse IO_ERROR = new StandardErrorResponse(400, "I/O error");

    /**
     * This response indicates that no route is known for the requested URL. It usually indicates a bug in the
     * client, such as a typo in the URL.
     */
    public static final StandardErrorResponse UNKNOWN_URL = new StandardErrorResponse(404, "unknown URL");

    /**
     * This response indicates that the route is known in principle, but contains an ID that does not exist. This can
     * happen routinely when requesting entities that are not known in advance to exist.
     */
    public static final StandardErrorResponse ID_NOT_FOUND = new StandardErrorResponse(404, "ID not found");

    /**
     * Most endpoints expect a JSON request body (if they expect a request body at all), so here is a standard
     * response for other request content types.
     */
    public static final StandardErrorResponse JSON_EXPECTED = new StandardErrorResponse(415, "expected application/json content type");

    /**
     * The standard response for "something went wrong in the server", usually an uncaught exception.
     */
    public static final StandardErrorResponse INTERNAL_SERVER_ERROR = new StandardErrorResponse(500, "internal server error");

    /**
     * This is an error response for failed request body validation. The error lists the fields that failed
     * validation and their error messages.
     */
    public static StandardErrorResponse requestBodyValidationFailed(JsonValidationException e) {
        List<Field> translatedErrors = new ArrayList<>();
        for (FieldErrorNode.FlattenedError flattenedError : e.getFieldErrorNode().flatten()) {
            translatedErrors.add(new Field(flattenedError.getPathAsString(), flattenedError.message()));
        }
        return new StandardErrorResponse(400, "invalid request body", translatedErrors);
    }

    // ----------------------------------------------------------------------------------------------------------------

    public StandardErrorResponse(int status, String message) {
        this(status, message, List.of());
    }

    public StandardErrorResponse(int status, String message, Field field) {
        this(status, message, List.of(field));
    }

    @Override
    public void transmit(ResponseTransmitter responseTransmitter) throws IOException {
        responseTransmitter.setStatus(status);
        responseTransmitter.setContentType("application/json");
        responseTransmitter.writeJson(new Body(message, fields));
    }

    public record Field(String path, String message) {}
    public record Body(String message, List<Field> fields) {
        public Body {
            fields = List.copyOf(fields);
        }
    }
}
