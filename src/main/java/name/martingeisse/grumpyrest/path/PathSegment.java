package name.martingeisse.grumpyrest.path;

public abstract class PathSegment {

    public static PathSegment parse(String segmentSpec) {
        return segmentSpec.startsWith(":")
                ? new VariablePathSegment(segmentSpec.substring(1))
                : new LiteralPathSegment(segmentSpec);
    }

    public abstract boolean matches(String segment);

}
