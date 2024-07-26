package io.github.grumpystuff.grumpyrest.response.standard;

import io.github.grumpystuff.grumpyrest.response.Response;
import io.github.grumpystuff.grumpyrest.response.ResponseTransmitter;

import java.util.Objects;

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
        Objects.requireNonNull(responseTransmitter, "responseTransmitter");

        responseTransmitter.setStatus(status);
    }

}
