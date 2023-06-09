package name.martingeisse.grumpyjson.builtin.json;

/**
 * This can be used to implement a property that must be null during validation, and always serializes as null. Such
 * a property obviously does not add any information to the data structure, but might be useful to maintain
 * compatibility with a field that was used in previous versions.
 *
 * This can be wrapped in JsonOptional to allow the field to be absent or null, as well as control whether the field
 * gets serialized as absent or null.
 *
 * A shared instance is provided to reduce memory usage, but creating new instances is fine as well.
 */
public record JsonMustBeNull() {
    public static final JsonMustBeNull INSTANCE = new JsonMustBeNull();
}
