package io.github.grumpystuff.grumpyjson;

import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializerProvider;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializerProvider;

/**
 * Combines JsonSerializerProvider and JsonDeserializerProvider. This interface is typically used by converters
 * that need to look up dependency converters.
 */
public interface JsonProviders extends JsonSerializerProvider, JsonDeserializerProvider {
}
