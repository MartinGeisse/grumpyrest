/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.path;

/**
 * A single segment from a {@link Path}.
 */
public abstract class PathSegment {

    /**
     * Parses an instance from a single segment of a string-based path specification. The segment is turned
     * into a path parameter ({@link VariablePathSegment}) if the segment specification starts with a <code>:</code>
     * character.
     *
     * @param segmentSpec the segment specification
     * @return the parsed segment object
     */
    public static PathSegment parse(String segmentSpec) {
        return segmentSpec.startsWith(":")
                ? new VariablePathSegment(segmentSpec.substring(1))
                : new LiteralPathSegment(segmentSpec);
    }

    /**
     * Checks whether this segment matches a segment of a path from an incoming request.
     *
     * @param segment the request path segment
     * @return true if the segment matches, false if not
     */
    public abstract boolean matches(String segment);

}
