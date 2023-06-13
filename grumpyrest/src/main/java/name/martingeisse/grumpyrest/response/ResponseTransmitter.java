package name.martingeisse.grumpyrest.response;

import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyjson.JsonGenerationException;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * This interface is used by the {@link HttpResponse} to transmit itself to the client.
 */
public interface ResponseTransmitter {

    // ----------------------------------------------------------------------------------------------------------------
    // header methods
    // ----------------------------------------------------------------------------------------------------------------

    void setStatus(int status);

    // note: a charset / encoding specified here has no effect on the response body since the caller has to specify
    // the body as bytes, so no encoding happens inside the response transmitter either way.
    void setContentType(String contentType);

    void addCustomHeader(String name, String value);

    // ----------------------------------------------------------------------------------------------------------------
    // body methods -- once used, no header methods may be used anymore
    // ----------------------------------------------------------------------------------------------------------------

    // after getting the output stream, none of the above methods may be called anymore.
    OutputStream getOutputStream() throws IOException;

    // does not set the Content-Type
    void writeJson(Object value) throws JsonGenerationException, IOException;

    // does not set the Content-Type
    void writeJson(Object value, TypeToken<?> typeToken) throws JsonGenerationException, IOException;

    // does not set the Content-Type
    void writeJson(Object value, Type type) throws JsonGenerationException, IOException;

}
