/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.response.standard;

import name.martingeisse.grumpyjson.FieldErrorNode;
import name.martingeisse.grumpyjson.JsonDeserializationException;
import name.martingeisse.grumpyrest.response.Response;
import name.martingeisse.grumpyrest.response.ResponseTransmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements a standard JSON-based error response format, combined with a selectable HTTP status code.
 * <p>
 * The "message" field contains a string message, and the "fields" contains a list of field errors, each with a
 * field path and message. This allows to send a response that mentions errors in multiple fields at the same time.
 * If, for example, JSON validation fails for the request body, then the individual field errors can be mentioned
 * to the client all at once.
 *
 * @param status  the HTTP status
 * @param message the error message
 * @param fields  the field errors
 */
public record StandardErrorResponse(int status, String message, List<Field> fields) implements Response {

    /**
     * Compact constructor.
     *
     * @param status  the HTTP status
     * @param message the error message
     * @param fields  the field errors
     */
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
     *
     * @param e the exceptionfrom the failed validation
     * @return the instance of this class
     */
    public static StandardErrorResponse requestBodyValidationFailed(JsonDeserializationException e) {
        List<Field> translatedErrors = new ArrayList<>();
        for (FieldErrorNode.FlattenedError flattenedError : e.getFieldErrorNode().flatten()) {
            translatedErrors.add(new Field(flattenedError.getPathAsString(), flattenedError.message()));
        }
        return new StandardErrorResponse(400, "invalid request body", translatedErrors);
    }

    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor without field errors.
     *
     * @param status  the HTTP status code
     * @param message the error message
     */
    public StandardErrorResponse(int status, String message) {
        this(status, message, List.of());
    }

    /**
     * Constructor with a single field error.
     *
     * @param status  the HTTP status code
     * @param message the error message
     * @param field the field error
     */
    public StandardErrorResponse(int status, String message, Field field) {
        this(status, message, List.of(field));
    }

    @Override
    public void transmit(ResponseTransmitter responseTransmitter) throws IOException {
        responseTransmitter.setStatus(status);
        responseTransmitter.setContentType("application/json");
        responseTransmitter.writeJson(new Body(message, fields));
    }

    /**
     * Represents an error for a single field.
     *
     * @param path    the field path
     * @param message the error message
     */
    public record Field(String path, String message) {}

    // helper type so the HTTP status code won't be included in the response body
    record Body(String message, List<Field> fields) {
        public Body {
            fields = List.copyOf(fields);
        }
    }
}
