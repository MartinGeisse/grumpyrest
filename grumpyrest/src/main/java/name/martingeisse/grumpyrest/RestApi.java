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
import name.martingeisse.grumpyrest.response.Response;
import name.martingeisse.grumpyrest.response.ResponseFactory;
import name.martingeisse.grumpyrest.response.ResponseFactoryRegistry;
import name.martingeisse.grumpyrest.response.standard.IdentityResponseFactory;
import name.martingeisse.grumpyrest.response.standard.JsonResponseFactory;
import name.martingeisse.grumpyrest.response.standard.NullResponseFactory;
import name.martingeisse.grumpyrest.response.standard.StandardErrorResponse;
import name.martingeisse.grumpyrest.stringparser.FromStringParser;
import name.martingeisse.grumpyrest.stringparser.FromStringParserRegistry;
import name.martingeisse.grumpyrest.stringparser.standard.IntegerFromStringParser;
import name.martingeisse.grumpyrest.stringparser.standard.StringFromStringParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class RestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestApi.class);

    private final List<Route> routes = new ArrayList<>();
    private final ResponseFactoryRegistry responseFactoryRegistry = new ResponseFactoryRegistry();
    private final JsonEngine jsonEngine = new JsonEngine();
    private final FromStringParserRegistry fromStringParserRegistry = new FromStringParserRegistry();
    private final QuerystringParserRegistry querystringParserRegistry = new QuerystringParserRegistry(fromStringParserRegistry);

    public RestApi() {

        // HTTP response factories
        addResponseFactory(new IdentityResponseFactory());
        addResponseFactory(new JsonResponseFactory());
        addResponseFactory(new NullResponseFactory());

        // from-string parsers
        addFromStringParser(new StringFromStringParser());
        addFromStringParser(new IntegerFromStringParser());

    }

    public void addRoute(Route route) {
        routes.add(route);
    }

    public void addComplexRoute(Path path, ComplexHandler handler) {
        addRoute(new Route(path, handler));
    }

    public void addComplexRoute(String path, ComplexHandler handler) {
        addRoute(new Route(path, handler));
    }

    public void addRoute(Path path, SimpleHandler handler) {
        addRoute(new Route(path, handler));
    }

    public void addRoute(String path, SimpleHandler handler) {
        addRoute(new Route(path, handler));
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void addResponseFactory(ResponseFactory responseFactory) {
        responseFactoryRegistry.add(responseFactory);
    }

    public ResponseFactoryRegistry getResponseFactoryRegistry() {
        return responseFactoryRegistry;
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

    public void handle(RequestCycle requestCycle) {
        try {

            // run the handler
            Object responseValue;
            try {
                Route route = match(requestCycle);
                if (route != null) {
                    requestCycle.setMatchedRoute(route);
                    responseValue = route.handle(requestCycle);
                } else {
                    responseValue = StandardErrorResponse.UNKNOWN_URL;
                }
            } catch (Exception e) {
                responseValue = e;
            }

            // run the HTTP response factory
            Response response;
            try {
                response = responseFactoryRegistry.createResponse(requestCycle, responseValue);
            } catch (Exception e) {
                LOGGER.error("could not create HTTP response for response value", e);
                response = StandardErrorResponse.INTERNAL_SERVER_ERROR;
            }

            // Transmit the response. Catching exceptions here is not useful because the response body has already been
            // started so we cannot change the status line anymore.
            response.transmit(requestCycle.getResponseTransmitter());

        } catch (Exception e) {
            // If we end up here, we cannot rely on the JSON serializer anymore (since that may the reason we ended
            // up here), so we just send a plain 500. If possible, we send an error text, but even that may fail (e.g.
            // something else requested a Writer even though everything here uses an OutputStream), so at least
            // defend against that.
            try {
                var responseTransmitter = requestCycle.getResponseTransmitter();
                responseTransmitter.setStatus(500);
                responseTransmitter.setContentType("application/text");
                responseTransmitter.getOutputStream().write("internal server error\n".getBytes(StandardCharsets.UTF_8));
            } catch (Exception e2) {
                // ignore
            }
        }
    }

}
