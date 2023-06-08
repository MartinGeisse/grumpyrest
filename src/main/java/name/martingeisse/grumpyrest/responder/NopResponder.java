package name.martingeisse.grumpyrest.responder;

import name.martingeisse.grumpyrest.RequestCycle;

/**
 * Does not respond at all. This is meant to handle cases where the handler has already sent a response manually.
 * If it didn't, then the default behavior from the servlet container will take place.
 */
public class NopResponder implements Responder {

    @Override
    public void respond(RequestCycle requestCycle) {
    }

}
