/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest;

import com.google.gson.JsonElement;
import name.martingeisse.grumpyjson.JsonEngine;
import name.martingeisse.grumpyjson.JsonRegistry;
import name.martingeisse.grumpyjson.JsonTypeAdapter;
import name.martingeisse.grumpyrest.request.HttpMethod;
import name.martingeisse.grumpyrest.request.path.Path;
import name.martingeisse.grumpyrest.request.querystring.QuerystringParser;
import name.martingeisse.grumpyrest.request.querystring.QuerystringParserRegistry;
import name.martingeisse.grumpyrest.response.FinishRequestException;
import name.martingeisse.grumpyrest.response.NoResponseFactoryException;
import name.martingeisse.grumpyrest.response.Response;
import name.martingeisse.grumpyrest.response.ResponseFactory;
import name.martingeisse.grumpyrest.response.ResponseFactoryRegistry;
import name.martingeisse.grumpyrest.response.ResponseValueWrapper;
import name.martingeisse.grumpyrest.response.standard.IdentityResponseFactory;
import name.martingeisse.grumpyrest.response.standard.JsonResponseFactory;
import name.martingeisse.grumpyrest.response.standard.NullResponseFactory;
import name.martingeisse.grumpyrest.response.standard.StandardErrorResponse;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParser;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParserRegistry;
import name.martingeisse.grumpyrest.request.stringparser.standard.IntegerFromStringParser;
import name.martingeisse.grumpyrest.request.stringparser.standard.StringFromStringParser;
import name.martingeisse.grumpyrest.response.standard.StatusOnlyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the main Entry point into grumpyrest. It is used by the application to define the REST API in terms
 * of endpoints and supported data types.
 */
public final class RestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestApi.class);

    private final List<Route> routes = new ArrayList<>();
    private final ResponseFactoryRegistry responseFactoryRegistry = new ResponseFactoryRegistry();
    private final JsonEngine jsonEngine = new JsonEngine();
    private final FromStringParserRegistry fromStringParserRegistry = new FromStringParserRegistry();
    private final QuerystringParserRegistry querystringParserRegistry = new QuerystringParserRegistry(fromStringParserRegistry);

    /**
     * Constructor.
     * <p>
     * No API routes will be registered by default, not even a route for the root path. You can add routes / handlers
     * after construction. You can also add a route that handles all unmatched requests, to implement a custom "not
     * found" response -- but make sure to add this route last, because it should match all requests, and then no
     * other route after than can be matched anymore.
     * <p>
     * Unmatched requests: By default, no fallback route for unmatched requests is present. Instead, the API handles
     * such requests internally by sending a standard 404 response that cannot be changed. This is just a formal
     * way to specify that a manually added catch-all route is the one and only way to implement custom "not found"
     * behavior.
     * <p>
     * From-string parsers, JSON Type adapters and response factories: After construction, this API object will have
     * default from-string parsers (for path / querystring parameters) and default response factories registered. It
     * will also use a newly created {@link JsonRegistry} which has default type adapters registered. All of these
     * defaults can be removed to support special cases that, for example, use different rules / formats or require a
     * more lenient parser. In normal cases, the defaults support standard types out of the box and support for
     * application types can be added without removing any of the standard implementations.
     */
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

    /**
     * Adds a route to handle requests. This route can only be matched by requests that do not match any previously
     * added route, and takes precedence over any route added later.
     * <p>
     * The {@link Route} object defines both which requests it will match as well as the handler to invoke for such
     * requests.
     *
     * @param route the route to add
     */
    public void addRoute(Route route) {
        routes.add(route);
    }

    /**
     * Adds a route to handle requests. This route can only be matched by requests that do not match any previously
     * added route, and takes precedence over any route added later.
     *
     * @param method the HTTP method to match
     * @param path the path to match
     * @param handler the handler to invoke
     */
    public void addComplexRoute(HttpMethod method, Path path, ComplexHandler handler) {
        addRoute(new Route(method, path, handler));
    }

    /**
     * Adds a route to handle requests. This route can only be matched by requests that do not match any previously
     * added route, and takes precedence over any route added later.
     *
     * @param method the HTTP method to match
     * @param path the path to match
     * @param handler the handler to invoke
     */
    public void addComplexRoute(HttpMethod method, String path, ComplexHandler handler) {
        addRoute(new Route(method, path, handler));
    }

    /**
     * Adds a route to handle requests. This route can only be matched by requests that do not match any previously
     * added route, and takes precedence over any route added later.
     *
     * @param method the HTTP method to match
     * @param path the path to match
     * @param handler the handler to invoke
     */
    public void addRoute(HttpMethod method, Path path, SimpleHandler handler) {
        addRoute(new Route(method, path, handler));
    }

    /**
     * Adds a route to handle requests. This route can only be matched by requests that do not match any previously
     * added route, and takes precedence over any route added later.
     *
     * @param method the HTTP method to match
     * @param path the path to match
     * @param handler the handler to invoke
     */
    public void addRoute(HttpMethod method, String path, SimpleHandler handler) {
        addRoute(new Route(method, path, handler));
    }

    /**
     * Returns a snapshot of the currently present routes.
     *
     * @return the routes, as an immutable snapshot
     */
    public List<Route> getRoutes() {
        return List.copyOf(routes);
    }

    /**
     * Adds a {@link ResponseFactory} to support new kinds of response values. This includes exception types for
     * which a specific response shall be generated (by default, exceptions just cause a standard 500 response).
     * <p>
     * Note that support for new JSON-able types should not be implemented as a response factory, but by
     * registering a {@link JsonTypeAdapter} with the {@link JsonRegistry} returned by {@link #getJsonEngine()} /
     * {@link JsonEngine#getRegistry()}.
     * <p>
     * Since most responses are expected to be JSON-based, custom response factories are rare. If you think you need
     * one, consider these alternatives:
     * <ul>
     *     <li>A custom {@link JsonTypeAdapter}, as mentioned above, to achieve a custom response format</li>
     *     <li>A {@link StandardErrorResponse} to send error responses in standard JSON format</li>
     *     <li>Building and returning a {@link JsonElement} (in case your response format does not have to
     *       be abstracted and re-usable</li>
     *     <li>Throwing a {@link FinishRequestException} as a means of control flow, to stop handling the requests and
     *       to return any of the above to the framework immediately</li>
     *     <li>A {@link StatusOnlyResponse} for custom HTTP-level errors</li>
     *     <li>Returning a {@link Response} directly (in case you need a custom response, but it does not have to be
     *       abstracted and re-usable).</li>
     * </ul>
     * One example where none of these is sufficient and you'll want a custom response factory is when you want to
     * define a custom RedirectException that sens a 302 redirect to the client. Reasons you need a custom response
     * factory in this case are:
     * <ul>
     *     <li>The problem is not JSON-related, so neither a {@link JsonTypeAdapter} nor {@link JsonElement} will help</li>
     *     <li>The {@link StandardErrorResponse} format is not related to the HTTP status nor the Location header,
     *       and if the redirect works, the response body should be irrelevant to the client</li>
     *     <li>The {@link FinishRequestException} actually helps with the control flow, but not with generating the
     *       response. You might want to derive RedirectException from {@link FinishRequestException}, or use a
     *       Redirection response wrapped by a {@link FinishRequestException} instead (more work for the caller,
     *       though), or have the RedirectException implement {@link ResponseValueWrapper} to kind-of make it its
     *       own response factory. But let's say, you don't want that for some reason.</li>
     *     <li>A {@link StatusOnlyResponse} only sets the status code. It won't add a Location header.</li>
     *     <li>A {@link Response} puts the burden of generating a correct 302 redirect on the caller.</li>
     * </ul>
     * In this case, you'll want to make RedirectResponse as abstract and as convenient for the caller as possible.
     * For example, you might define it in such a way that the caller does not have to specify the full URL to redirect
     * to, but only higher-level information from which the URL is derived. A custom {@link ResponseFactory} is then
     * a good way to generate a {@link Response} for that exception.
     *
     * @param responseFactory the response factory to register
     */
    public void addResponseFactory(ResponseFactory responseFactory) {
        responseFactoryRegistry.add(responseFactory);
    }

    /**
     * Getter method for the registry for custom response factories. See {@link #addResponseFactory(ResponseFactory)}
     * for some information on when such a factory helps. Getting the registry itself is necessary when you want to
     * remove the default response factories present after construction.
     *
     * @return the response factory registry
     */
    public ResponseFactoryRegistry getResponseFactoryRegistry() {
        return responseFactoryRegistry;
    }

    /**
     * Adds a from-string parser to support new types of path parameters and querystring parameters. This is needed,
     * for example, to support a custom date format in the path or querystring. Note that you might need to call
     * {@link #getFromStringParserRegistry()} to remove standard parsers if you want that custom format to be mapped
     * to one of the built-in types such as {@link LocalDate}.
     * <p>
     * Each from-string parser will only see a single path argument or a single querystring argument. If you have to
     * support whole-path logic, you will have to do so in the handler. If you have to support a whole-querystring
     * parser that uses a different format than key/value pairs separated by &amp; and = characters, see
     * {@link #getQuerystringParserRegistry()}.
     *
     * @param parser the parser to add
     */
    public void addFromStringParser(FromStringParser parser) {
        fromStringParserRegistry.addParser(parser);
    }

    /**
     * Getter method for the registry for from-string parsers. See {@link #addFromStringParser(FromStringParser)} for
     * some information on when such a parser helps. Getting the registry itself is necessary when you want to
     * remove the default parsers after construction.
     *
     * @return the from-string parser registry
     */
    public FromStringParserRegistry getFromStringParserRegistry() {
        return fromStringParserRegistry;
    }

    /**
     * Getter method for the registry for whole-querystring parsers. <b>Dealing with this registry is rarely needed
     * because it relates to how the whole querystring gets parsed, not how individual fields are parsed!</b> If you
     * want to support custom types for querystring parameters, see {@link #addFromStringParser(FromStringParser)}
     * (and possibly {@link #getFromStringParserRegistry()}) instead.
     * <p>
     * The registry returned here is relevant for redefining how the whole querystring is interpreted as individual
     * fields. You will have to deal with it in the following cases:
     * <ul>
     *     <li>If the querystring uses a custom format instead of the standard key/value list using &amp; and
     *       = characters (Note: I just realized this isn't possible right now because that format is imposed
     *       by the servlet API. Fortunately, we don't need it yet. If we do, it's time to change the
     *       {@link QuerystringParser} interface)</li>
     *     <li>If the type to parse the whole querystring as cannot use an auto-generated record parser, for example
     *       because it cannot be a Java record for some reason</li>
     * </ul>
     * There is no "addQuerystringParser()" method just so nobody is confused and thinks that you need to use it to
     * support custom field types.
     *
     * @return the whole querystring parser registry
     */
    public QuerystringParserRegistry getQuerystringParserRegistry() {
        return querystringParserRegistry;
    }

    /**
     * Getter method for the {@link JsonEngine}. This method is needed to add custom types for request/response bodies
     * using custom {@link JsonTypeAdapter}s.
     * <p>
     * (We might consider adding convenience methods to add type adapters here in RestApi)
     *
     * @return the JSON registries
     */
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
