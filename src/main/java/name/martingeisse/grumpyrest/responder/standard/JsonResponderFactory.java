package name.martingeisse.grumpyrest.responder.standard;

import name.martingeisse.grumpyrest.RequestCycle;
import name.martingeisse.grumpyrest.responder.Responder;
import name.martingeisse.grumpyrest.responder.ResponderFactory;

public final class JsonResponderFactory implements ResponderFactory {

    @Override
    public Responder createResponder(RequestCycle requestCycle, Object value) {
        if (value == null || !requestCycle.getApi().getJsonEngine().supportsType(value.getClass())) {
            return null;
        }
        return createResponderForSupportedValue(value);
    }

    private Responder createResponderForSupportedValue(Object value) {
        return requestCycle -> {
            var response = requestCycle.getResponse();
            response.setStatus(200);
            response.setContentType("application/json");
            // use an OutputStream because we don't rely on the servlet container to handle the encoding correctly
            requestCycle.getApi().getJsonEngine().writeTo(value, response.getOutputStream());
        };
    }

}
