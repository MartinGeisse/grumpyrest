package name.martingeisse.grumpyjson;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * These classes allow to quickly construct error information, then propagate it upwards while collecting the field
 * path, and finally generate one or more error messages as strings or JSON from it.
 */
public abstract class FieldErrorNode {

    private FieldErrorNode() {
    }

    public static FieldErrorNode create(String message) {
        return new Message(message);
    }

    public static FieldErrorNode create(Throwable cause) {
        return create("an exception occurred: " + cause);
    }

    public final FieldErrorNode and(FieldErrorNode other) {
        return other == null ? this : new Siblings(this, other);
    }

    public final FieldErrorNode in(String fieldName) {
        return new Field(fieldName, this);
    }

    public final ImmutableList<FlattenedError> flatten() {
        List<FlattenedError> errors = new ArrayList<>();
        List<String> segments = new ArrayList<>();
        flatten(errors, segments);
        return ImmutableList.copyOf(errors);
    }

    protected abstract void flatten(List<FlattenedError> errors, List<String> segments);

    public static final class Message extends FieldErrorNode {

        private final String message;

        private Message(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        protected void flatten(List<FlattenedError> errors, List<String> segments) {
            errors.add(new FlattenedError(message, ImmutableList.copyOf(segments)));
        }

    }

    public static final class Siblings extends FieldErrorNode {

        private final FieldErrorNode first;
        private final FieldErrorNode second;

        private Siblings(FieldErrorNode first, FieldErrorNode second) {
            this.first = first;
            this.second = second;
        }

        public FieldErrorNode getFirst() {
            return first;
        }

        public FieldErrorNode getSecond() {
            return second;
        }

        @Override
        protected void flatten(List<FlattenedError> errors, List<String> segments) {
            first.flatten(errors, segments);
            second.flatten(errors, segments);
        }

    }

    public static final class Field extends FieldErrorNode {

        private final String name;
        private final FieldErrorNode node;

        private Field(String name, FieldErrorNode node) {
            this.name = name;
            this.node = node;
        }

        public String getName() {
            return name;
        }

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

    public record FlattenedError(String message, ImmutableList<String> fieldPath) {

        // this constructor is very useful in unit tests
        public FlattenedError(String message, String... pathSegments) {
            this(message, ImmutableList.copyOf(pathSegments));
        }

        public String getPathAsString() {
            return fieldPath.isEmpty() ? "<root>" : StringUtils.join(fieldPath.toArray(), '.');
        }

        @Override
        public String toString() {
            return "at field " + getPathAsString() + ": " + message;
        }

    }

}
