package name.martingeisse.grumpyjson;

import java.util.ArrayList;
import java.util.List;

/**
 * This exception type gets thrown when the incoming JSON does not match the expected structure.
 *
 * TODO field paths, multiple errors etc.
 */
public class JsonValidationException extends Exception {

    private final List<String> reverseStackAccumulator = new ArrayList<>();

    public JsonValidationException() {
    }

    public JsonValidationException(String message) {
        super(message);
    }

    public JsonValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonValidationException(Throwable cause) {
        super(cause);
    }

    public List<String> getReverseStackAccumulator() {
        return reverseStackAccumulator;
    }

}
