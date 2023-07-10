/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class typically gets constructed by grumpyjson and consumed by grumpyrest, without any need for the application
 * code to deal with it. Application code can, however, <ul>
 *     <li>get instances after an error has occurred, to access the field path and message programmatically</li>
 *     <li>create instances inside custom type adapters to expose the field path for nestable custom types</li>
 * </ul>
 * <p>
 * This class and its subclasses allow to quickly construct error information, then propagate it upwards while
 * collecting the field path, and finally generate one or more error messages as strings or JSON from it.
 * <p>
 * An error is generated from a specific JSON value, e.g. from a JSON string whose format does not match a validation
 * pattern. Such an error is created with a message, but does not know the context (field path) in which it occurs,
 * nor does it know about other errors that may occur in other fields. The error is then wrapped in an exception
 * (either {@link JsonDeserializationException} or {@link JsonSerializationException}) and thrown to propagate it upwards.
 * Container values further up then process these exceptions and re-throw them, collecting multiple errors and
 * augmenting their field paths. The result is a tree structure of FieldErrorNode that contains all errors that have
 * occurred in the whole JSON structure. This tree structure is then usually flattened to a list of errors and
 * returned to the REST client.
 * <p>
 * Instances of this class and its subclasses are immutable. All grouping and nesting is done by creating new nodes
 * around the existing nodes.
 * <p>
 * Instances of this class should be created using factory methods. The subclass constructors are private to enforce
 * this.
 */
public abstract class FieldErrorNode {

    private FieldErrorNode() {
    }

    /**
     * Creates a node for a single error message without a field path.
     *
     * @param message the error message
     * @return the newly created node
     */
    public static FieldErrorNode create(String message) {
        return new Message(message);
    }

    /**
     * Creates a node for a single error message without a field path.
     *
     * @param exception an exception that caused the error
     * @return the newly created node
     */
    public static FieldErrorNode create(Exception exception) {
        return new InternalException(exception);
    }

    /**
     * Groups two error sets together in a single new node. This and the other node may both contain multiple errors
     * and possibly use field path suffixes. If this node is later used below another field path prefix, then that
     * prefix applies to all errors from this and the other node.
     *
     * @param other the other node to group together with this one
     * @return the node that groups both nodes
     */
    public final FieldErrorNode and(FieldErrorNode other) {
        return other == null ? this : new Siblings(this, other);
    }

    /**
     * Adds the nesting within a single field as a prefix to the field path of this node, returning the result as
     * a new node. The nesting affects all errors stored under this node.
     * <p>
     * Note that if a prefix consisting of multiple nesting levels is to be added, then this method must be called
     * multiple times in reverse order (i.e. from innermost field to outermost field).
     *
     * @param fieldName the name of the field to add as a prefix
     * @return the node that has the prefix applied
     */
    public final FieldErrorNode in(String fieldName) {
        return new Field(fieldName, this);
    }

    /**
     * Flattens the errors contained in this node and its subnodes as a list of {@link FlattenedError} objects.
     *
     * @return the flattened errors
     */
    public final List<FlattenedError> flatten() {
        List<FlattenedError> errors = new ArrayList<>();
        List<String> segments = new ArrayList<>();
        flatten(errors, segments);
        return List.copyOf(errors);
    }

    /**
     * NOT PUBLIC API
     *
     * @param errors ...
     * @param segments ...
     */
    protected abstract void flatten(List<FlattenedError> errors, List<String> segments);

    /**
     * This method turns the node to a string for simple output / debugging purposes. It does not guarantee a specific
     * format of the string. To process errors programmatically, use {@link #flatten()} to get the contained errors in
     * a more reliable format, or process the nodes directly.
     *
     * @return this node as a string
     */
    @Override
    public String toString() {
        return StringUtil.join(flatten(), "; ");
    }

    /**
     * A leaf node with an error message.
     */
    public static final class Message extends FieldErrorNode {

        private final String message;

        private Message(String message) {
            this.message = message;
        }

        /**
         * Getter for the error message
         *
         * @return the error message, without any field information
         */
        public String getMessage() {
            return message;
        }

        @Override
        protected void flatten(List<FlattenedError> errors, List<String> segments) {
            errors.add(new FlattenedError(message, segments));
        }

    }

    /**
     * A leaf node with an internal exception. The exception message and details will be hidden from the client, so
     * we don't leak any internal information, but we want to make it available internally for debugging. The client
     * will see the field path and a generic error message and so might be able to guess what the problem is.
     * <p>
     * This node type only handles {@link Exception}, not {@link Throwable} in general. The latter will not be caught
     * at all since they usually indicate much more fundamental problems.
     */
    public static final class InternalException extends FieldErrorNode {

        private final Exception exception;

        private InternalException(Exception exception) {
            this.exception = exception;
        }

        /**
         * Getter for the exception.
         *
         * @return the exception, without any field information.
         */
        public Exception getException() {
            return exception;
        }

        @Override
        protected void flatten(List<FlattenedError> errors, List<String> segments) {
            errors.add(new FlattenedError(ExceptionMessages.INTERNAL_ERROR, segments));
        }

    }

    /**
     * A node that groups two subtrees together to allow the tree to store multiple errors.
     */
    public static final class Siblings extends FieldErrorNode {

        private final FieldErrorNode first;
        private final FieldErrorNode second;

        private Siblings(FieldErrorNode first, FieldErrorNode second) {
            this.first = first;
            this.second = second;
        }

        /**
         * Getter for the first subtree.
         *
         * @return the first subtree
         */
        public FieldErrorNode getFirst() {
            return first;
        }

        /**
         * Getter for the second subtree.
         *
         * @return the second subtree
         */
        public FieldErrorNode getSecond() {
            return second;
        }

        @Override
        protected void flatten(List<FlattenedError> errors, List<String> segments) {
            first.flatten(errors, segments);
            second.flatten(errors, segments);
        }

    }

    /**
     * A node that specifies a single field nesting level for its subtree.
     */
    public static final class Field extends FieldErrorNode {

        private final String name;
        private final FieldErrorNode node;

        private Field(String name, FieldErrorNode node) {
            this.name = name;
            this.node = node;
        }

        /**
         * Getter method for the field name under which the subtree is nested.
         *
         * @return the field name
         */
        public String getName() {
            return name;
        }

        /**
         * Getter method for the local root node of the subtree which is nested under the field.
         *
         * @return the subtree root
         */
        public FieldErrorNode getNode() {
            return node;
        }

        @Override
        protected void flatten(List<FlattenedError> errors, List<String> segments) {
            segments.add(name);
            node.flatten(errors, segments);
            segments.remove(segments.size() - 1);
        }
    }

    /**
     * This structure represents a single error, i.e. a leaf node, and contains the error message of the {@link Message}
     * node as well as the field path as a list of the field names of all {@link Field} nodes on the way from the
     * root to the message leaf node.
     * <p>
     * Since every {@link Siblings} node allows for two different paths to take, a whole tree must be represented as
     * a _list_ of flattened errors.
     *
     * @param message the error message
     * @param fieldPath the field names, from root to leaf
     */
    public record FlattenedError(String message, List<String> fieldPath) {

        /**
         * Constructor
         *
         * @param message the error message
         * @param fieldPath the field names, from root to leaf
         */
        public FlattenedError {
            fieldPath = List.copyOf(fieldPath);
        }

        /**
         * this constructor is very useful in unit tests
         *
         * @param message the error message
         * @param pathSegments the field names, from root to leaf
         */
        public FlattenedError(String message, String... pathSegments) {
            this(message, List.of(pathSegments));
        }

        /**
         * Returns the field path as a dot-separated string
         *
         * @return the field path
         */
        public String getPathAsString() {
            return fieldPath.isEmpty() ? "(root)" : StringUtil.join(fieldPath, ".");
        }

        @Override
        public String toString() {
            return "at field " + getPathAsString() + ": " + message;
        }

    }

}
