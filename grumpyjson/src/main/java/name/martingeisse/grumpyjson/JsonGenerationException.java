/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import name.martingeisse.grumpyjson.builtin.ListAdapter;
import name.martingeisse.grumpyjson.builtin.helper_types.NullableFieldAdapter;
import name.martingeisse.grumpyjson.builtin.helper_types.OptionalFieldAdapter;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * This exception type gets thrown when the state of the objects being serialized is not possible to map to JSON.
 * This can happen if an object is in an inconsistent state (which should have been prevented by the object's class),
 * or it can happen if the object is in a consistent state that does not have an equivalent representation in JSON.
 * <p>
 * This exception therefore always indicates a bug. If the object's class allows its state to become inconsistent,
 * then this should be prevented. On the other hand, if the object's state has no equivalent JSON representation,
 * then the cause that wants to turn it into JSON anyway is faulty. Because it is a bug, and is therefore unexpected,
 * this class extends {@link RuntimeException}, not {@link Exception}.
 */
public class JsonGenerationException extends RuntimeException {

    /**
     * needs javadoc because this class is {@link java.io.Serializable}
     */
    private final FieldErrorNode fieldErrorNode;

    /**
     * Creates an exception for a single error message without a field path. This constructor is typically used in
     * {@link JsonTypeAdapter#toJson(Object, Type)} at the specific place where an inconsistent or non-JSON-able value
     * was found.
     *
     * @see FieldErrorNode#create(String)
     *
     * @param message the error message
     */
    public JsonGenerationException(String message) {
        this(FieldErrorNode.create(message));
    }

    /**
     * Creates an exception for a single-field error caused by an internal exception from the type adapter. Application
     * code will rarely use this constructor directly because it can just throw the internal exception and have the
     * framework wrap it. It is only needed when implementing type adapters for custom structured types or custom
     * wrapper types, to implement the catch-and-wrap there.
     * <p>
     * For examples on how this method is useful, see {@link NullableFieldAdapter} and {@link OptionalFieldAdapter}.
     *
     * @see FieldErrorNode#create(Exception)
     *
     * @param cause the internal exception
     */
    public JsonGenerationException(Exception cause) {
        this(FieldErrorNode.create(cause));
    }

    /**
     * Creates an exception for a {@link FieldErrorNode} that wraps one or more actual errors. Application code will
     * usually not call this method directly. It is only needed when implementing type adapters for custom structured
     * types, to re-throw after the individual field errors have been combined and field paths applied.
     * <p>
     * For an example on how this method is useful, see {@link RecordAdapter} and {@link ListAdapter}.
     *
     * @param fieldErrorNode the node that contains one or more actual errors
     */
    public JsonGenerationException(FieldErrorNode fieldErrorNode) {
        super("exception during JSON generation");
        this.fieldErrorNode = Objects.requireNonNull(fieldErrorNode, "fieldErrorNode");
    }

    /**
     * Getter for the {@link FieldErrorNode} that holds the actual error(s)
     *
     * @return the field error node
     */
    public FieldErrorNode getFieldErrorNode() {
        return fieldErrorNode;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + ": " + fieldErrorNode;
    }
}
