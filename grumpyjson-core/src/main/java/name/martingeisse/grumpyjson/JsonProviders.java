package name.martingeisse.grumpyjson;

import name.martingeisse.grumpyjson.deserialize.JsonDeserializerProvider;
import name.martingeisse.grumpyjson.serialize.JsonSerializerProvider;

/**
 * Combines JsonSerializerProvider and JsonDeserializerProvider. This interface is typically used by converters
 * that need to look up dependency converters.
 */
public interface JsonProviders extends JsonSerializerProvider, JsonDeserializerProvider {
}
