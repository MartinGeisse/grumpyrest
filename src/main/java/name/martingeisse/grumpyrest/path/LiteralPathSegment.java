package name.martingeisse.grumpyrest.path;

public final class LiteralPathSegment extends PathSegment {

    private final String text;

    public LiteralPathSegment(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean matches(String segment) {
        return text.equals(segment);
    }

}
