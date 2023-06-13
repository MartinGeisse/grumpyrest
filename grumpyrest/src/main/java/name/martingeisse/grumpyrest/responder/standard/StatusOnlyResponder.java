package name.martingeisse.grumpyrest.responder.standard;

import name.martingeisse.grumpyrest.ResponseTransmitter;
import name.martingeisse.grumpyrest.responder.Responder;

/**
 * Sends an empty response with a configurable HTTP status code.
 */
public final class StatusOnlyResponder implements Responder {

    private final int status;

    public StatusOnlyResponder(int status) {
        this.status = status;
    }

    @Override
    public void respond(ResponseTransmitter responseTransmitter) {
        responseTransmitter.setStatus(status);
    }

}
