package name.martingeisse.grumpyrest;

import name.martingeisse.grumpyjson.JsonEngine;
import name.martingeisse.grumpyrest.path.Path;
import name.martingeisse.grumpyrest.responder.*;
import name.martingeisse.grumpyrest.responder.standard.IdentityResponderFactory;
import name.martingeisse.grumpyrest.responder.standard.JsonResponderFactory;
import name.martingeisse.grumpyrest.responder.standard.StatusOnlyResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class RestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestApi.class);

    private final List<Route> routes = new ArrayList<>();
    private final ResponderFactoryRegistry responderFactoryRegistry = new ResponderFactoryRegistry();
    private final JsonEngine jsonEngine = new JsonEngine();

    public RestApi() {
        responderFactoryRegistry.add(new IdentityResponderFactory());
        responderFactoryRegistry.add(new JsonResponderFactory(jsonEngine));
    }

    public void addRoute(Route route) {
        routes.add(route);
    }

    public void addRoute(Path path, Handler handler) {
        addRoute(new Route(path, handler));
    }

    public void addRoute(String path, Handler handler) {
        addRoute(Path.parse(path), handler);
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void addResponderFactory(ResponderFactory responderFactory) {
        responderFactoryRegistry.add(responderFactory);
    }

    public ResponderFactoryRegistry getResponderFactoryRegistry() {
        return responderFactoryRegistry;
    }

    public JsonEngine getJsonEngine() {
        return jsonEngine;
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
            LOGGER.error("could not create responder for response value", e);
            responder = new StatusOnlyResponder(500);
        }

        // generate the response. Catching exceptions here is not useful because the response has already been started
        // so we cannot change the status line anymore.
        responder.respond(requestCycle);

    }

}
