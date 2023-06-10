/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest;

import name.martingeisse.grumpyjson.JsonEngine;
import name.martingeisse.grumpyrest.path.Path;
import name.martingeisse.grumpyrest.querystring.QuerystringParserRegistry;
import name.martingeisse.grumpyrest.responder.Responder;
import name.martingeisse.grumpyrest.responder.ResponderFactory;
import name.martingeisse.grumpyrest.responder.ResponderFactoryRegistry;
import name.martingeisse.grumpyrest.responder.standard.IdentityResponderFactory;
import name.martingeisse.grumpyrest.responder.standard.JsonResponderFactory;
import name.martingeisse.grumpyrest.responder.standard.StandardErrorResponder;
import name.martingeisse.grumpyrest.stringparser.FromStringParser;
import name.martingeisse.grumpyrest.stringparser.FromStringParserRegistry;
import name.martingeisse.grumpyrest.stringparser.standard.StringFromStringParser;
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
    private final FromStringParserRegistry fromStringParserRegistry = new FromStringParserRegistry();
    private final QuerystringParserRegistry querystringParserRegistry = new QuerystringParserRegistry(fromStringParserRegistry);

    public RestApi() {

        // responder factories
        addResponderFactory(new IdentityResponderFactory());
        addResponderFactory(new JsonResponderFactory());

        // from-string parsers
        addFromStringParser(new StringFromStringParser());

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

    public void addFromStringParser(FromStringParser parser) {
        fromStringParserRegistry.addParser(parser);
    }

    public FromStringParserRegistry getFromStringParserRegistry() {
        return fromStringParserRegistry;
    }

    // note: no addQuerystringParser() so people don't think they have to do that routinely to add new QS field types --
    // that is only needed to add new whole-QS parsers.
    public QuerystringParserRegistry getQuerystringParserRegistry() {
        return querystringParserRegistry;
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
                requestCycle.setMatchedRoute(route);
                responseValue = route.handle(requestCycle);
            } else {
                responseValue = StandardErrorResponder.UNKNOWN_URL;
            }
        } catch (Exception e) {
            responseValue = e;
        }

        // run the responder factory
        Responder responder;
        try {
            responder = responderFactoryRegistry.createResponder(requestCycle, responseValue);
        } catch (Exception e) {
            LOGGER.error("could not create responder for response value", e);
            responder = StandardErrorResponder.INTERNAL_SERVER_ERROR;
        }

        // generate the response. Catching exceptions here is not useful because the response has already been started
        // so we cannot change the status line anymore.
        responder.respond(requestCycle);

    }

}
