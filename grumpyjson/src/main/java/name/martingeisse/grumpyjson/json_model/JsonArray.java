package name.martingeisse.grumpyjson.json_model;

import java.util.List;

/**
 * This class represents JSON arrays.
 */
public abstract class JsonArray extends JsonElement {

    /**
     * Creates an instance of this class from a {@link List} containing the elements of the JSON array.
     *
     * @param elements the elements of the JSON array to create
     * @return the JSON array
     */
    public static JsonArray of(List<JsonElement> elements) {
        return new VariableLenthArray(List.copyOf(elements));
    }

    /**
     * Creates an instance of this class from elements passed as varargs.
     *
     * @param elements the elements of the JSON array to create
     * @return the JSON array
     */
    public static JsonArray of(JsonElement... elements) {
        return new VariableLenthArray(List.of(elements));
    }

    /**
     * Returns the elements of this JSON array as a {@link List}.
     *
     * @return the elements
     */
    public abstract List<JsonElement> getAsList();

    @Override
    public final List<JsonElement> deserializerExpectsArray() {
        return getAsList();
    }

    private static final class VariableLenthArray extends JsonArray {

        private final List<JsonElement> elements;

        VariableLenthArray(List<JsonElement> elements) {
            this.elements = elements;
        }

        @Override
        public List<JsonElement> getAsList() {
            return elements;
        }

    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JsonArray a && a.getAsList().equals(getAsList());
    }

    @Override
    public int hashCode() {
        return getAsList().hashCode();
    }

    @Override
    public String toString() {
        return "JSON:" + getAsList();
    }

}
