package name.martingeisse.grumpyrest.response.standard;

import name.martingeisse.grumpyrest.response.ResponseTransmitter;
import name.martingeisse.grumpyrest.response.Response;

/**
 * Sends an empty response with a configurable HTTP status code.
 */
public final class StatusOnlyResponse implements Response {

    private final int status;

    /**
     * Constructor.
     *
     * @param status the HTTP status code
     */
    public StatusOnlyResponse(int status) {
        this.status = status;
    }

    @Override
    public void transmit(ResponseTransmitter responseTransmitter) {
        responseTransmitter.setStatus(status);
    }

}
