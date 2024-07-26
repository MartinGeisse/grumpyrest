/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.deserialize;

import io.github.grumpystuff.grumpyjson.FieldErrorNode;
import io.github.grumpystuff.grumpyjson.builtin.ListConverter;
import io.github.grumpystuff.grumpyjson.builtin.helper_types.NullableFieldConverter;
import io.github.grumpystuff.grumpyjson.builtin.helper_types.OptionalFieldConverter;
import io.github.grumpystuff.grumpyjson.builtin.record.RecordConverter;
import io.github.grumpystuff.grumpyjson.json_model.JsonElement;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * This exception type gets thrown when the incoming JSON does not match the expected structure.
 */
public class JsonDeserializationException extends Exception {

    /**
     * needs javadoc because this class is {@link java.io.Serializable}
     */
    private final FieldErrorNode fieldErrorNode;

    /**
     * Creates an exception for a single error message without a field path. This constructor is typically used in
     * {@link JsonDeserializer#deserialize(JsonElement, Type)} for a JSON primitive, such as a parser for a custom date
     * format that has encountered a string which does not match the expected pattern.
     * 
     * @see FieldErrorNode#create(String) 
     *
     * @param message the error message
     */
    public JsonDeserializationException(String message) {
        this(FieldErrorNode.create(message));
    }

    /**
     * Creates an exception for a single-field error caused by an exception from the deserializer. Application
     * code will rarely use this constructor directly because it can just throw that exception and have the
     * framework wrap it. It is only needed when implementing deserializers for custom structured types or custom
     * wrapper types, to implement the catch-and-wrap there.
     * <p>
     * For examples on how this method is useful, see {@link NullableFieldConverter} and {@link OptionalFieldConverter}.
     *
     * @see FieldErrorNode#create(Exception)
     *
     * @param cause the exception to wrap
     */
    public JsonDeserializationException(Exception cause) {
        this(FieldErrorNode.create(cause));
    }

    /**
     * Creates an exception for a {@link FieldErrorNode} that wraps one or more actual errors. Application code will
     * usually not call this method directly. It is only needed when implementing deserializers for custom structured
     * types, to re-throw after the individual field errors have been combined and field paths applied.
     * <p>
     * For an example on how this method is useful, see {@link RecordConverter} and {@link ListConverter}.
     *
     * @param fieldErrorNode the node that contains one or more actual errors
     */
    public JsonDeserializationException(FieldErrorNode fieldErrorNode) {
        super("exception during JSON deserialization");
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
