package name.martingeisse.grumpyrest.response.standard;

import name.martingeisse.grumpyrest.response.ResponseTransmitter;
import name.martingeisse.grumpyrest.response.HttpResponse;

/**
 * Sends an empty response with a configurable HTTP status code.
 */
public final class StatusOnlyResponse implements HttpResponse {

    private final int status;

    public StatusOnlyResponse(int status) {
        this.status = status;
    }

    @Override
    public void transmit(ResponseTransmitter responseTransmitter) {
        responseTransmitter.setStatus(status);
    }

}
