package name.martingeisse.grumpyjson.json_model;

import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;

import java.util.List;
import java.util.Map;

/**
 * Base class for all JSON elements.
 * <p>
 * Code that wants to build a JSON structure, such as a serializer, should use the .of() factory methods in the
 * subclasses of this class to create JSON nodes.
 * </p>
 * Code that wants to analyze a JSON structure, such as a deserializer, has two options:
 * <ul>
 *     <li>If only a single kind of JSON node is valid for the value to be deserialized, use one of the
 *     deserializerExpectsXXX methods to check for the correct type of node, throw an exception if another kind of
 *     node is present, and obtain the values from that node.</li>
 *     <li>If multiple kinds of JSON nodes are valid for the value to be serialized, use instanceof with pattern
 *     matching like this: <code>if (myJsonElement instanceof JsonString s) {...s.getValue()...}</code></li>
 * </ul>
 * <p>
 * This class and all its subclasses are immutable. They do not share lists and maps with that they are constructed
 * from, and all lists and maps they return are immutable. To guarantee immutability, these classes do make one
 * assumption: The {@link Number}s from which {@link JsonNumber}s get constructed must be immutable too --
 * unfortunately, Java does not guarantee that on its own, and mutable implementations exist.
 * <p>
 * Note: This class does not use the term "primitive", and in particular does not provide a method such as
 * <code>isPrimitive()</code>, to avoid confusion because different JSON libraries disagree on whether null is a
 * primitive.
 * <p>
 * Implementations support equals() / hashCode() / toString(), although without regard to performance. While all these
 * methods are rarely needed for JSON processing, they are useful in unit tests. The toString() method is intentionally
 * written in such a way that the output is NOT valid JSON, so it is not accidentally used to generate JSON -- doing so
 * would be slow and likely handle some edge cases inccorrectly.
 */
public abstract class JsonElement {

    // only allow our own subclasses
    JsonElement() {
    }

    /**
     * If this JSON element is not JSON null, this method throws a {@link JsonDeserializationException}, otherwise
     * it does nothing.
     *
     * @throws JsonDeserializationException if this JSON element is not JSON null
     */
    public void deserializerExpectsNull() throws JsonDeserializationException {
        throw new JsonDeserializationException("expected null, found: " + this);
    }

    /**
     * If this JSON element is not a JSON boolean, this method throws a {@link JsonDeserializationException}, otherwise
     * it returns the boolean value.
     *
     * @return the boolean value
     * @throws JsonDeserializationException if this JSON element is not a JSON boolean
     */
    public boolean deserializerExpectsBoolean() throws JsonDeserializationException {
        throw new JsonDeserializationException("expected boolean, found: " + this);
    }

    /**
     * If this JSON element is not a JSON number, this method throws a {@link JsonDeserializationException}, otherwise
     * it returns the numeric value.
     *
     * @return the numeric value
     * @throws JsonDeserializationException if this JSON element is not a JSON number
     */
    public Number deserializerExpectsNumber() throws JsonDeserializationException {
        throw new JsonDeserializationException("expected number, found: " + this);
    }

    /**
     * If this JSON element is not a JSON string, this method throws a {@link JsonDeserializationException}, otherwise
     * it returns the string value.
     *
     * @return the string value
     * @throws JsonDeserializationException if this JSON element is not a JSON string
     */
    public String deserializerExpectsString() throws JsonDeserializationException {
        throw new JsonDeserializationException("expected string, found: " + this);
    }

    /**
     * If this JSON element is not a JSON array, this method throws a {@link JsonDeserializationException}, otherwise
     * it returns the array's elements as a {@link List}.
     *
     * @return the list of elements
     * @throws JsonDeserializationException if this JSON element is not a JSON array
     */
    public List<JsonElement> deserializerExpectsArray() throws JsonDeserializationException {
        throw new JsonDeserializationException("expected array, found: " + this);
    }

    /**
     * If this JSON element is not a JSON object, this method throws a {@link JsonDeserializationException}, otherwise
     * it returns the array's elements as a {@link Map}.
     *
     * @return the property map
     * @throws JsonDeserializationException if this JSON element is not a JSON object
     */
    public Map<String, JsonElement> deserializerExpectsObject() throws JsonDeserializationException {
        throw new JsonDeserializationException("expected object, found: " + this);
    }

}
