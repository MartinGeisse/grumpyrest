package name.martingeisse.grumpyrest.responder;

import name.martingeisse.grumpyrest.RequestCycle;

import java.io.IOException;

public interface Responder {

    /**
     * Writes a response.
     */
    void respond(RequestCycle requestCycle) throws IOException;

}
