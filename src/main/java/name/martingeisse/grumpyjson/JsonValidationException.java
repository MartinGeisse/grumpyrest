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

}
