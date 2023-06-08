package name.martingeisse.grumpyrest;

import name.martingeisse.grumpyrest.responder.Responder;
import name.martingeisse.grumpyrest.responder.ResponderFactory;
import name.martingeisse.grumpyrest.responder.ResponderFactoryRegistry;
import name.martingeisse.grumpyrest.responder.StatusOnlyResponder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class RestApi {

    private final List<Route> routes = new ArrayList<>();
    private final ResponderFactoryRegistry responderFactoryRegistry = new ResponderFactoryRegistry();

    public void add(Route route) {
        routes.add(route);
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void add(ResponderFactory responderFactory) {
        responderFactoryRegistry.add(responderFactory);
    }

    public ResponderFactoryRegistry getResponderFactoryRegistry() {
        return responderFactoryRegistry;
    }

    public Route match(RequestCycle requestCycle) {
        for (Route route : routes) {
            if (route.path().matchesSegments(requestCycle.getPathSegments())) {
                return route;
            }
        }
        return null;
    }

    public void handle(RequestCycle requestCycle) throws IOException {

        // run the handler
        Object responseValue;
        try {
            Route route = match(requestCycle);
            if (route != null) {
                responseValue = route.handle(requestCycle);
            } else {
                responseValue = new StatusOnlyResponder(404);
            }
        } catch (Exception e) {
            responseValue = e;
        }

        // run the responder factory
        Responder responder;
        try {
            responder = responderFactoryRegistry.createResponder(responseValue);
        } catch (Exception e) {
            responder = new StatusOnlyResponder(500);
        }

        // generate the response. Catching exceptions here is not useful because the response has already been started
        // so we cannot change the status line anymore.
        responder.respond(requestCycle);

    }

}
