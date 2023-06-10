/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

/**
 * This exception type gets thrown when the incoming JSON does not match the expected structure.
 */
public class JsonValidationException extends Exception {

    public FieldErrorNode fieldErrorNode;

    public JsonValidationException(String message) {
        this(FieldErrorNode.create(message));
    }

    public JsonValidationException(Throwable cause) {
        this(FieldErrorNode.create(cause));
    }

    public JsonValidationException(FieldErrorNode fieldErrorNode) {
        super("exception during JSON validation");
        this.fieldErrorNode = fieldErrorNode;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + ": " + fieldErrorNode;
    }
}
