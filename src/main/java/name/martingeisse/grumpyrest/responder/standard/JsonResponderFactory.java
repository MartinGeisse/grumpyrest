package name.martingeisse.grumpyrest.responder.standard;

import name.martingeisse.grumpyjson.JsonEngine;
import name.martingeisse.grumpyrest.responder.Responder;
import name.martingeisse.grumpyrest.responder.ResponderFactory;

public final class JsonResponderFactory implements ResponderFactory {

    private final JsonEngine jsonEngine;

    public JsonResponderFactory(JsonEngine jsonEngine) {
        this.jsonEngine = jsonEngine;
    }

    @Override
    public Responder createResponder(Object value) {
        if (value == null || !jsonEngine.supportsType(value.getClass())) {
            return null;
        }
        return requestCycle -> {
            var response = requestCycle.getResponse();
            response.setStatus(200);
            response.setContentType("application/json");
            // use an OutputStream because we don't rely on the servlet container to handle the encoding correctly
            jsonEngine.writeTo(value, response.getOutputStream());
        };
    }

}
