package name.martingeisse.grumpyrest;

import name.martingeisse.grumpyrest.request.PathArgument;

import java.util.List;

public record RouteMatchResult(Route route, List<PathArgument> pathArguments) {

    public RouteMatchResult {
        pathArguments = List.copyOf(pathArguments);
    }

}
