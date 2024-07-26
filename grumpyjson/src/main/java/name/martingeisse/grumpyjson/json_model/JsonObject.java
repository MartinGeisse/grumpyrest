package name.martingeisse.grumpyjson.json_model;

import java.util.Map;

/**
 * This class represents JSON objects.
 */
public abstract class JsonObject extends JsonElement {

    /**
     * Creates an instance of this class from a {@link Map} containing the properties of the JSON object.
     *
     * @param properties the properties of the JSON object to create
     * @return the JSON object
     */
    public static JsonObject of(Map<String, JsonElement> properties) {
        return new VariableSizeObject(Map.copyOf(properties));
    }

    /**
     * Creates an empty instance of this class.
     *
     * @return the JSON object
     */
    public static JsonObject of() {
        return new VariableSizeObject(Map.of());
    }

    /**
     * Creates an instance of this class from one directly specified key/value pair.
     *
     * @param key1 the first property key
     * @param value1 the first property value
     * @return the JSON object
     */
    public static JsonObject of(String key1, JsonElement value1) {
        return new VariableSizeObject(Map.of(key1, value1));
    }

    /**
     * Creates an instance of this class from two directly specified key/value pairs.
     *
     * @param key1 the first property key
     * @param value1 the first property value
     * @param key2 the second property key
     * @param value2 the second property value
     * @return the JSON object
     */
    public static JsonObject of(String key1, JsonElement value1, String key2, JsonElement value2) {
        return new VariableSizeObject(Map.of(key1, value1, key2, value2));
    }

    /**
     * Creates an instance of this class from three directly specified key/value pairs.
     *
     * @param key1 the first property key
     * @param value1 the first property value
     * @param key2 the second property key
     * @param value2 the second property value
     * @param key3 the second property key
     * @param value3 the second property value
     * @return the JSON object
     */
    public static JsonObject of(String key1, JsonElement value1, String key2, JsonElement value2, String key3, JsonElement value3) {
        return new VariableSizeObject(Map.of(key1, value1, key2, value2, key3, value3));
    }

    // only allow our own subclasses
    private JsonObject() {
    }

    /**
     * Returns the properties of this JSON object as a {@link Map}.
     *
     * @return the properties
     */
    public abstract Map<String, JsonElement> getAsMap();

    @Override
    public final Map<String, JsonElement> deserializerExpectsObject()  {
        return getAsMap();
    }

    private static final class VariableSizeObject extends JsonObject {

        private final Map<String, JsonElement> properties;

        VariableSizeObject(Map<String, JsonElement> properties) {
            this.properties = properties;
        }

        @Override
        public Map<String, JsonElement> getAsMap() {
            return properties;
        }

    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JsonObject o && o.getAsMap().equals(getAsMap());
    }

    @Override
    public int hashCode() {
        return getAsMap().hashCode();
    }

    @Override
    public String toString() {
        return "JSON:" + getAsMap();
    }

}
