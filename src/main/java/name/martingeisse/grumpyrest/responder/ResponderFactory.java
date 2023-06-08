package name.martingeisse.grumpyrest.responder;

import name.martingeisse.grumpyrest.RequestCycle;

public interface ResponderFactory {

    /**
     * Returns null on failure, causing the next factory to be tried.
     */
    Responder createResponder(RequestCycle requestCycle, Object value);

}
