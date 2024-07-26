package name.martingeisse.grumpyjson.json_model;

/**
 * This class represents the JSON values true and false.
 */
public final class JsonBoolean extends JsonElement {

    /**
     * The JSON true value
     */
    public static final JsonBoolean TRUE = new JsonBoolean(true);

    /**
     * The JSON false value
     */
    public static final JsonBoolean FALSE = new JsonBoolean(false);

    /**
     * Returns an instance of this class for a plain boolean value.
     *
     * @param value the boolean value
     * @return the JSON boolean for that value
     */
    public static JsonBoolean of(boolean value) {
        return value ? TRUE : FALSE;
    }

    private final boolean value;

    private JsonBoolean(boolean value) {
        this.value = value;
    }

    /**
     * Getter method.
     *
     * @return the value of this JSON boolean
     */
    public boolean getValue() {
        return value;
    }

    @Override
    public boolean deserializerExpectsBoolean() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JsonBoolean b && b.value == value;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override
    public String toString() {
        return value ? "JSON:true" : "JSON:false";
    }

}
