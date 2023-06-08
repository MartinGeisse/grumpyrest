package name.martingeisse.grumpyrest.finish;

import name.martingeisse.grumpyrest.RequestCycle;
import name.martingeisse.grumpyrest.responder.Responder;

import java.io.IOException;

public record StandardErrorResponder(int status, String message) implements Responder {

    @Override
    public void respond(RequestCycle requestCycle) throws IOException {
        var response = requestCycle.getResponse();
        response.setStatus(status);
        response.setContentType("application/json");

        Body body = new Body(message);
        requestCycle.getApi().getJsonEngine().writeTo(body, response.getOutputStream());
    }

    public record Body(String message) {}
}
