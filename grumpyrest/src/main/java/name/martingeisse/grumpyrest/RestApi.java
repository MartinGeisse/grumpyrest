/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest;

import name.martingeisse.grumpyjson.JsonEngine;
import name.martingeisse.grumpyrest.request.HttpMethod;
import name.martingeisse.grumpyrest.request.path.Path;
import name.martingeisse.grumpyrest.request.querystring.QuerystringParserRegistry;
import name.martingeisse.grumpyrest.response.NoResponseFactoryException;
import name.martingeisse.grumpyrest.response.Response;
import name.martingeisse.grumpyrest.response.ResponseFactory;
import name.martingeisse.grumpyrest.response.ResponseFactoryRegistry;
import name.martingeisse.grumpyrest.response.standard.IdentityResponseFactory;
import name.martingeisse.grumpyrest.response.standard.JsonResponseFactory;
import name.martingeisse.grumpyrest.response.standard.NullResponseFactory;
import name.martingeisse.grumpyrest.response.standard.StandardErrorResponse;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParser;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParserRegistry;
import name.martingeisse.grumpyrest.request.stringparser.standard.IntegerFromStringParser;
import name.martingeisse.grumpyrest.request.stringparser.standard.StringFromStringParser;
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

    // region configuration

    public void addRoute(Route route) {
        routes.add(route);
    }

    public void addComplexRoute(HttpMethod method, Path path, ComplexHandler handler) {
        addRoute(new Route(method, path, handler));
    }

    public void addComplexRoute(HttpMethod method, String path, ComplexHandler handler) {
        addRoute(new Route(method, path, handler));
    }

    public void addRoute(HttpMethod method, Path path, SimpleHandler handler) {
        addRoute(new Route(method, path, handler));
    }

    public void addRoute(HttpMethod method, String path, SimpleHandler handler) {
        addRoute(new Route(method, path, handler));
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

    // endregion

    // region run-time

    /**
     * Matches the specified request cycle against all routes. This will not apply the match result to the request
     * cycle, i.e. not bind path arguments.
     * <p>
     * If multiple routes match, then the one that was first added to this API will be returned.
     *
     * @param requestCycle the request cycle to match
     * @return if a route matched, the match result for that route. Otherwise null.
     */
    public RouteMatchResult match(RequestCycle requestCycle) {
        for (Route route : routes) {
            RouteMatchResult result = route.match(requestCycle);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Handles a request cycle. This first matches the request cycle against all routes to find the route that will
     * handle it, then apply information gathered from matching (i.e. the path arguments) to the request cycle. It
     * will then  invoke the handler from the matched route to perform application logic and obtain a response value.
     * This response value gets mapped to a response using an appropriate factory. Finally, the response will be
     * transmitted to the client.
     *
     * @param requestCycle the request cycle to handle
     */
    public void handle(RequestCycle requestCycle) {
        try {

            // run the handler
            Object responseValue;
            try {
                RouteMatchResult matchResult = match(requestCycle);
                if (matchResult != null) {
                    requestCycle.applyRouteMatchResult(matchResult);
                    responseValue = matchResult.route().invokeHandler(requestCycle);
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
            } catch (NoResponseFactoryException e) {
                response = StandardErrorResponse.INTERNAL_SERVER_ERROR;
                Object rejectedResponseValue = e.getResponseValue();
                if (rejectedResponseValue instanceof Throwable t) {
                    LOGGER.error("unexpected exception and no response factory registered for it", t);
                } else {
                    String hint = getHintForMissingResponseFactory(rejectedResponseValue);
                    if (hint == null) {
                        LOGGER.error(e.getMessage());
                    } else {
                        LOGGER.error(hint + " original error: " + e.getMessage());
                    }
                }
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

    private static String getHintForMissingResponseFactory(Object value) {
        if (value instanceof List<?>) {
            return "You returned a List object as the response value. A List must be wrapped in a TypeWrapper to indicate the element type.";
        }
        return null;
    }

    // endregion

}
