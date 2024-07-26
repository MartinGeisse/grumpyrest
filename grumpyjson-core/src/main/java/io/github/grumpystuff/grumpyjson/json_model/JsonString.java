package io.github.grumpystuff.grumpyjson.json_model;

/**
 * This class represents JSON strings.
 */
public final class JsonString extends JsonElement {

    /**
     * Creates an instance of this class for the specified plain string value.
     *
     * @param value the string value
     * @return the JSON string
     */
    public static JsonString of(String value) {
        return new JsonString(value);
    }

    private final String value;

    private JsonString(String value) {
        this.value = value;
    }

    /**
     * Getter method.
     *
     * @return the value of this JSON string
     */
    public String getValue() {
        return value;
    }

    @Override
    public String deserializerExpectsString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JsonString s && s.value.equals(value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "JSON:\"" + value + "\"";
    }

}
