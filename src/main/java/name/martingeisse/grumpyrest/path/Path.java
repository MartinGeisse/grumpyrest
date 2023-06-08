package name.martingeisse.grumpyrest.path;

import com.google.common.collect.ImmutableList;

public record Path(ImmutableList<PathSegment> segments) {

    // TODO allow match extra incoming segments

    public boolean matchesSegments(ImmutableList<String> argumentSegments) {
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
