package name.martingeisse.grumpyrest.path;

import java.util.Objects;

public final class VariablePathSegment extends PathSegment {

    private final String variableName;

    public VariablePathSegment(String variableName) {
        this.variableName = Objects.requireNonNull(variableName);
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public boolean matches(String segment) {
        Objects.requireNonNull(segment);
        return true;
    }

}
