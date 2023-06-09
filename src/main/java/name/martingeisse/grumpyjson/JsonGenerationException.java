package name.martingeisse.grumpyjson;

import java.util.ArrayList;
import java.util.List;

/**
 * This exception type gets thrown when the state of the objects being serialized is not possible to map to JSON.
 *
 * Such an error is always due to a bug (possible a bug in an earlier validation step), hence unexpected, so this
 * exception extends RuntimeException, not Exception.
 *
 * TODO field paths, multiple errors etc.
 */
public class JsonGenerationException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "exception during JSON generation";

    private final List<String> reverseStackAccumulator = new ArrayList<>();

    public JsonGenerationException() {
        super(DEFAULT_MESSAGE);
    }

    public JsonGenerationException(String message) {
        super(message);
    }

    public JsonGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonGenerationException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

    public List<String> getReverseStackAccumulator() {
        return reverseStackAccumulator;
    }

    public String fieldPathToString() {
        if (reverseStackAccumulator.isEmpty()) {
            return "<root>";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = reverseStackAccumulator.size() - 1; i >= 0; i--) {
            builder.append(reverseStackAccumulator.get(i));
            if (i > 0) {
                builder.append('.');
            }
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return getMessage() + " at field: " + fieldPathToString();
    }

    public static JsonGenerationException fieldIsNull() {
        return new JsonGenerationException("field is null");
    }

}
