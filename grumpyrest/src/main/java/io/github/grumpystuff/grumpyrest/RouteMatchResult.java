package io.github.grumpystuff.grumpyrest;

import io.github.grumpystuff.grumpyrest.request.PathArgument;

import java.util.List;
import java.util.Objects;

/**
 * The result returned by {@link RestApi#match(RequestCycle)} and {@link Route#match(RequestCycle)}.
 *
 * @param route the route that was matched
 * @param pathArguments arguments that were bound to path parameters
 */
public record RouteMatchResult(Route route, List<PathArgument> pathArguments) {

    /**
     * Compact constructor.
     *
     * @param route the route that was matched
     * @param pathArguments arguments that were bound to path parameters
     */
    public RouteMatchResult {
        Objects.requireNonNull(route, "route");
        Objects.requireNonNull(pathArguments, "pathArguments");

        pathArguments = List.copyOf(pathArguments);
    }

}
