package name.martingeisse.grumpyrest.responder;

public interface ResponderFactory {

    /**
     * Returns null on failure, causing the next factory to be tried.
     */
    Responder createResponder(Object value);

}
