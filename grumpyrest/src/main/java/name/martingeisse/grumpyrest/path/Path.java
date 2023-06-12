/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.path;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Path(ImmutableList<PathSegment> segments) {

    // TODO allow match extra incoming segments

    public static Path parse(String pathSpec) {
        Objects.requireNonNull(pathSpec);
        String[] segmentSpecs = PathUtil.splitIntoSegments(pathSpec);
        List<PathSegment> segments = new ArrayList<>();
        for (String segmentSpec : segmentSpecs) {
            segments.add(PathSegment.parse(segmentSpec));
        }
        return new Path(ImmutableList.copyOf(segments));
    }

    public boolean matchesSegments(ImmutableList<String> argumentSegments) {
        Objects.requireNonNull(argumentSegments);
        if (argumentSegments.size() != segments.size()) {
            return false;
        }
        for (int i = 0; i < argumentSegments.size(); i++) {
            if (!segments.get(i).matches(argumentSegments.get(i))) {
                return false;
            }
        }
        return true;
    }

}
