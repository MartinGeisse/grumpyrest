package name.martingeisse.grumpyjson.json_model;

/**
 * This class represents JSON null.
 */
public final class JsonNull extends JsonElement {

    /**
     * Shared instance of this class.
     */
    public static final JsonNull INSTANCE = new JsonNull();

    /**
     * This method just returns the shared instance {@link #INSTANCE} and is provided for uniformity with the other
     * JSON classes.
     *
     * @return the shared instance
     */
    public static JsonNull of() {
        return INSTANCE;
    }

    // only allow our own subclasses
    private JsonNull() {
    }

    @Override
    public void deserializerExpectsNull() {
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JsonNull;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "JSON:null";
    }

}
