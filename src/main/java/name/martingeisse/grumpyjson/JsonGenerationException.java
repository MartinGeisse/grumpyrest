/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

/**
 * This exception type gets thrown when the state of the objects being serialized is not possible to map to JSON.
 *
 * Such an error is always due to a bug (possible a bug in an earlier validation step), hence unexpected, so this
 * exception extends RuntimeException, not Exception.
 */
public class JsonGenerationException extends RuntimeException {

    public FieldErrorNode fieldErrorNode;

    public JsonGenerationException(String message) {
        this(FieldErrorNode.create(message));
    }

    public JsonGenerationException(Throwable cause) {
        this(FieldErrorNode.create(cause));
    }

    public JsonGenerationException(FieldErrorNode fieldErrorNode) {
        super("exception during JSON generation");
        this.fieldErrorNode = fieldErrorNode;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + ": " + fieldErrorNode;
    }
}
