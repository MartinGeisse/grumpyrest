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

}
