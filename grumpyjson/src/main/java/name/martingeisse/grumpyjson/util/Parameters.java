package name.martingeisse.grumpyjson.util;

/**
 * NOT PUBLIC API
 */
public final class Parameters {

    // prevent instantiation
    private Parameters() {
    }

    /**
     * NOT PUBLIC API
     *
     * @param value ...
     * @param name ...
     * @return ...
     * @param <T> ...
     */
    public static <T> T notNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException("parameter must not be null: " + name);
        }
        return value;
    }

}
