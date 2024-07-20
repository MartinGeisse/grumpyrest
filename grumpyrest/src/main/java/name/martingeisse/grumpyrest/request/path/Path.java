/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.path;

import name.martingeisse.grumpyrest.request.PathArgument;
import name.martingeisse.grumpyrest.request.stringparser.ParseFromStringService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A path -- actually a path <i>pattern</i> -- that determines whether the actual path provided by a request matches
 * a route. An instance of this class may define path parameters using {@link VariablePathSegment}.
 * <p>
 * It is currently not possible to create an instance that allows an incoming request path to have arbitrary extra
 * segments.
 *
 * @param segments the segments of this path (pattern). The segments of the actual path from a request are lined up
 *                 one-by-one with these segments and matched using {@link PathSegment#matches(String)}.
 */
public record Path(List<PathSegment> segments) {

    /**
     * Compact constructor.
     *
     * @param segments the segments of this path (pattern). The segments of the actual path from a request are lined up
     *                 one-by-one with these segments and matched using {@link PathSegment#matches(String)}.
     */
    public Path {
        segments = List.copyOf(segments);
    }

    /**
     * Creates an instance from a string-based specification, using <code>:name</code> for path parameters.
     *
     * @param pathSpec the path specification
     * @return the instance
     */
    public static Path parse(String pathSpec) {
        Objects.requireNonNull(pathSpec);
        String[] segmentSpecs = PathUtil.splitIntoSegments(pathSpec);
        List<PathSegment> segments = new ArrayList<>();
        for (String segmentSpec : segmentSpecs) {
            segments.add(PathSegment.parse(segmentSpec));
        }
        return new Path(segments);
    }

    /**
     * Matches a request path (i.e. a list of strings) against this path. This can either succeed and return a
     * list of bound path arguments, or fail because the paths are different. "Different" here means that a
     * literal segment of this path has a different text than the corresponding segment of the request.
     *
     * @param requestSegments the path segments from the request
     * @param parseFromStringService this service is needed because it is baked into returned path argument
     *                               objects to allow the application code to convert the arguments into
     *                               high-level types
     * @return if matched successfully, the bound path arguments. This list only contains an element for each
     * path parameter in this path. That is, it does not contain any entries for literal segments.
     */
    public List<PathArgument> match(List<String> requestSegments, ParseFromStringService parseFromStringService) {
        Objects.requireNonNull(requestSegments);
        Objects.requireNonNull(parseFromStringService, "parseFromStringService");
        if (requestSegments.size() != segments.size()) {
            return null;
        }
        List<PathArgument> pathArguments = new ArrayList<>();
        for (int i = 0; i < requestSegments.size(); i++) {
            PathSegment pathSegment = segments.get(i);
            String requestSegment = requestSegments.get(i);
            if (!pathSegment.matches(requestSegment)) {
                return null;
            }
            if (pathSegment instanceof VariablePathSegment variable) {
                pathArguments.add(new PathArgument(variable.getVariableName(), requestSegment, parseFromStringService));
            }
        }
        return pathArguments;
    }

    @Override
    public String toString() {
        if (segments.isEmpty()) {
            return "/";
        }
        StringBuilder builder = new StringBuilder();
        for (PathSegment segment : segments) {
            builder.append('/').append(segment);
        }
        return builder.toString();
    }

}
