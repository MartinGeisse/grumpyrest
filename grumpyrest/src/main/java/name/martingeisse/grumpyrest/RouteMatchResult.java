package name.martingeisse.grumpyrest;

import name.martingeisse.grumpyrest.request.PathArgument;

import java.util.List;

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
        pathArguments = List.copyOf(pathArguments);
    }

}
