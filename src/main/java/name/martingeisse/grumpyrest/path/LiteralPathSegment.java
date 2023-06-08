package name.martingeisse.grumpyrest.path;

import java.util.Objects;

public final class LiteralPathSegment extends PathSegment {

    private final String text;

    public LiteralPathSegment(String text) {
        this.text = Objects.requireNonNull(text);
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean matches(String segment) {
        return text.equals(Objects.requireNonNull(segment));
    }

}
