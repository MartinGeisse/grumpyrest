/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.path;

public abstract class PathSegment {

    public static PathSegment parse(String segmentSpec) {
        return segmentSpec.startsWith(":")
                ? new VariablePathSegment(segmentSpec.substring(1))
                : new LiteralPathSegment(segmentSpec);
    }

    public abstract boolean matches(String segment);

}
