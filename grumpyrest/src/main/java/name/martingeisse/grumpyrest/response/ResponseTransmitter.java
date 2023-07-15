package name.martingeisse.grumpyrest.response;

import jakarta.servlet.http.HttpServletResponse;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This interface is used by the {@link Response} to transmit itself to the client. It is an abstraction of the
 * {@link HttpServletResponse}.
 * <p>
 * The methods in this interface are grouped into "header" methods and "body" methods. This is a consequence of two
 * aspects of HTTP: In HTTP, too, all headers must come before the body, and the body is usually so large that it is
 * not desirable to keep the body, or even parts of it, in memory. The result is that these methods must be called
 * in two "blocks" of calls: First, only header methods should be called, then only body methods should be called.
 * Formally speaking, it is not allowed to call a header method after calling a body method.
 */
public interface ResponseTransmitter {

    /**
     * Header Method: Sets the HTTP status code to send to the client.
     *
     * @param status the HTTP status
     */
    void setStatus(int status);

    /**
     * Header Method: Sets the Content-Type header to send to the client.
     * <p>
     * Note: Setting a Content-Type that includes a "charset" field (character encoding) has no effect on the
     * response body since the caller (the {@link Response}) has to specify the body as bytes, so all character
     * encoding happens in the caller. This behavior was defined like this because the implicit interactions --
     * regarding character encoding -- between the various setters in HttpServletRequest are a source of confusion.
     *
     * @param contentType the Content-Type to send to the client
     */
    void setContentType(String contentType);

    /**
     * Header Method: Adds a custom header to send to the client.
     *
     * @param name the name of the header
     * @param value the value of the header
     */
    void addCustomHeader(String name, String value);

    /**
     * Body method: Obtains the output stream to send body data to the client.
     *
     * @return the body output stream
     * @throws IOException on I/O errors
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Body method: Converts the specified value to JSON and sends it to the client using the body output stream.
     * <p>
     * This method does not set the Content-Type to JSON to keep the separation into header methods and body methods
     * clean.
     *
     * @param value the value to convert to JSON
     * @throws JsonSerializationException if the value is in an inconsistent state or in a state that cannot be converted to JSON
     * @throws IOException on I/O errors
     */
    void writeJson(Object value) throws JsonSerializationException, IOException;

}
