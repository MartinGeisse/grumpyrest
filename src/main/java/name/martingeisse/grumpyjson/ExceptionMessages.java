package name.martingeisse.grumpyjson;

/**
 * This class is mostly useful for unit tests, so we don't have to check for exceptions while working around
 * unknown error messages -- this way we just know these messages.
 */
public class ExceptionMessages {

    private ExceptionMessages() {
    }

    public static final String UNEXPECTED_PROPERTY = "unexpected property";
    public static final String MISSING_PROPERTY = "missing property";


}
