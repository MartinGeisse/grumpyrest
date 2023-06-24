package name.martingeisse.grumpyrest.response;

/**
 * This exception type gets thrown if no response factory can be found for a specific response value. The most common
 * case for this is that the response value is itself an unexpected exception. The response value will be hidden from
 * the client so we don't leak any secrets, but we want to log it, *especially* if it's an exception.
 */
public final class NoResponseFactoryException extends RuntimeException {

    private final Object responseValue;

    /**
     * Constructor.
     *
     * @param responseValue the response value for which no factory could be found
     */
    public NoResponseFactoryException(Object responseValue) {
        super("no ResponseFactory found for value: " + responseValue);
        if (responseValue instanceof Throwable t) {
            initCause(t);
        }
        this.responseValue = responseValue;
    }

    /**
     * Getter for the response value
     *
     * @return the response value
     */
    public Object getResponseValue() {
        return responseValue;
    }

}
