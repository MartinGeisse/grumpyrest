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

public record Route(HttpMethod method, Path path, ComplexHandler handler) {

    public Route(HttpMethod method, String path, ComplexHandler handler) {
        this(method, Path.parse(path), handler);
    }

    public Route(HttpMethod method, Path path, SimpleHandler handler) {
        this(method, path, (RequestCycle requestCycle) -> handler.handle(requestCycle.getHighlevelRequest()));
    }

    public Route(HttpMethod method, String path, SimpleHandler handler) {
        this(method, Path.parse(path), handler);
    }

    /**
     * Tries to match the specified request cycle against this route.
     *
     * @param requestCycle the request cycle that contains the request for matching
     * @return if matched, a request result that contains this route. Otherwise null.
     */
    public RouteMatchResult match(RequestCycle requestCycle) {
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
        return handler.handle(requestCycle);
    }

}
