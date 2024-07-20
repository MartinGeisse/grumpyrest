/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest;

import name.martingeisse.grumpyrest.request.HttpMethod;
import name.martingeisse.grumpyrest.request.PathArgument;
import name.martingeisse.grumpyrest.request.path.Path;
import name.martingeisse.grumpyrest.request.stringparser.ParseFromStringService;

import java.util.List;
import java.util.Objects;

/**
 * A route is, conceptually, a set of criteria that determine whether the route matches a specific requests, as well
 * as a handler that defines what to do for requests that match. The {@link RestApi} handles a request by picking the
 * first route that matches, then handing the request to that route. Routes are the main mechanism to distinguish
 * requests by HTTP method and path.
 * <p>
 * In a more practical sense,
 * <ul>
 *     <li>routes always distinguish by HTTP method. That is, you cannot (currently) define a route that matches
 *     multiple or all HTTP methods. You'll have to add multiple routes for that.</li>
 *     <li>each route uses a path (actually, a path pattern) and only handles requests that match this path. The
 *     path can contain literal path segments (strings that must match the request exactly) as well as path
 *     variables (a.k.a. path parameters) that will match any path segment from the request, and also capture the
 *     provided path segment for use in the handler. This is used mainly for paths that contain entity IDs.</li>
 *     <li>the handler is simply a callback that gets called if the route matches. There are two possible interfaces
 *     to implement as a handler, {@link SimpleHandler} and {@link ComplexHandler}. You should implement
 *     {@link SimpleHandler} if possible, and {@link ComplexHandler} if necessary, because it reduces coupling and
 *     simplifies testing and mocking. Refer to these interfaces for details</li>
 * </ul>
 * <p>
 * The canonical constructor takes a {@link ComplexHandler} because this is the more general case.
 *
 * @param method the HTTP method to match
 * @param path the path pattern to match. May include path parameters.
 * @param handler the handler to invoke for requests that match this route
 */
public record Route(HttpMethod method, Path path, ComplexHandler handler) {

    /**
     * Standard constructor.
     *
     * @param method the HTTP method to match
     * @param path the path pattern to match. May include path parameters.
     * @param handler the handler to invoke for requests that match this route
     */
    public Route {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(handler, "handler");
    }

    /**
     * This constructor specifies the path pattern as a string instead of a {@link Path} object. Leading and trailing
     * slashes are ignored. A path parameter is specified as a path segment that starts with a ':' character.
     *
     * @param method the HTTP method to match
     * @param path the path pattern to match. May include path parameters.
     * @param handler the handler to invoke for requests that match this route
     */
    public Route(HttpMethod method, String path, ComplexHandler handler) {
        this(method, Path.parse(path), handler);
    }

    /**
     * Constructor for a {@link SimpleHandler}.
     *
     * @param method the HTTP method to match
     * @param path the path pattern to match. May include path parameters.
     * @param handler the handler to invoke for requests that match this route
     */
    public Route(HttpMethod method, Path path, SimpleHandler handler) {
        this(method, path, (RequestCycle requestCycle) -> handler.handle(requestCycle.getHighlevelRequest()));
        Objects.requireNonNull(handler, "handler");
    }

    /**
     * Constructor for a {@link SimpleHandler} which also specifies the path pattern as a string instead of a
     * {@link Path} object. Leading and trailing slashes are ignored. A path parameter is specified as a path segment
     * that starts with a ':' character.
     *
     * @param method the HTTP method to match
     * @param path the path pattern to match. May include path parameters.
     * @param handler the handler to invoke for requests that match this route
     */
    public Route(HttpMethod method, String path, SimpleHandler handler) {
        this(method, Path.parse(path), handler);
    }

    /**
     * Tries to match the specified request cycle against this route.
     *
     * @param requestCycle the request cycle that contains the request for matching
     * @return if matched, a match result that contains this route. Otherwise null.
     */
    public RouteMatchResult match(RequestCycle requestCycle) {
        Objects.requireNonNull(requestCycle, "requestCycle");

        if (!method.matches(requestCycle.getServletRequest().getMethod())) {
            return null;
        }
        ParseFromStringService parseFromStringService = requestCycle.getApi().getFromStringParserRegistry();
        List<PathArgument> pathArguments = path.match(requestCycle.getPathSegments(), parseFromStringService);
        if (pathArguments == null) {
            return null;
        }
        return new RouteMatchResult(this, pathArguments);
    }

    /**
     * Calls the handler for the specified request cycle. This should only be done if the request cycle has been
     * successfully matched against this route and the match result has been applied to the request cycle -- otherwise,
     * the request cycle will not contain path arguments which the handler will likely want to use.
     *
     * @param requestCycle the request cycle to pass to the handler
     * @return the response value
     * @throws Exception on errors -- will be treated like a response value.
     */
    public Object invokeHandler(RequestCycle requestCycle) throws Exception {
        Objects.requireNonNull(requestCycle, "requestCycle");

        return handler.handle(requestCycle);
    }

}
