/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.response;

import jakarta.servlet.http.HttpServletResponse;
import name.martingeisse.grumpyjson.JsonRegistries;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;
import name.martingeisse.grumpyrest.RequestCycle;

import java.io.IOException;

/**
 * An object that knows how to express itself via HTTP to the client. In other words, this interface is implemented
 * by objects that can transmit themselves to the client using a {@link ResponseTransmitter}.
 * <p>
 * Most application-level objects do not implement this interface because they are defined on a higher level than
 * HTTP. These objects do not even know how to express themselves as JSON, but they <i>do</i> have a
 * {@link JsonSerializer} registered in the {@link JsonRegistries}, so at least that can be used to generate JSON.
 * However, this is not by itself sufficient to express via HTTP.
 * <p>
 * Instead, on a lower level, a {@link ResponseFactory} is selected that further converts the JSON to a
 * {@link Response}. Likewise, other values such as uncaught exceptions are also converted to a {@link Response},
 * either by an appropriate factory or by the framework itself. The result is that no matter what happens, in the
 * end an instance of this interface is the result, and that instance is asked to transmit itself to the client.
 * <p>
 * The response transmitter used here is simply an abstraction of the {@link HttpServletResponse} that we use to
 * decouple our code from unnecessary parts of the servlet spec.
 */
public interface Response {

    /**
     * Transmits this response using the specified response transmitter.
     *
     * Note: This method only takes the response transmitter. If the implementation has to access other things from the
     * {@link RequestCycle} to transmit itself, e.g. access request parameters on-the-fly, then these things have to be
     * passed to the implementation by another mechanism. This interface tries to keep the dependency between the two
     * minimal.
     *
     * @param responseTransmitter the response transmitter that is used to actually send data to the client
     * @throws IOException on I/O errors
     */
    void transmit(ResponseTransmitter responseTransmitter) throws IOException;

}
