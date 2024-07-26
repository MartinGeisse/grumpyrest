package name.martingeisse.grumpyjson.gson;

import name.martingeisse.grumpyjson.json_model.*;
import name.martingeisse.grumpyjson.util.Parameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class GsonTreeMapper {

    // prevent instantiation
    private GsonTreeMapper() {
    }

    static JsonElement mapFromGson(com.google.gson.JsonElement gsonElement) {
        Parameters.notNull(gsonElement, "gsonElement");

        if (gsonElement.isJsonNull()) {
            return JsonNull.INSTANCE;
        } else if (gsonElement.isJsonPrimitive()) {
            com.google.gson.JsonPrimitive primitive = gsonElement.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return JsonBoolean.of(primitive.getAsBoolean());
            } else if (primitive.isNumber()) {
                return JsonNumber.of(primitive.getAsNumber());
            } else if (primitive.isString()) {
                return JsonString.of(primitive.getAsString());
            } else {
                throw new IllegalArgumentException("unknown primitive type: " + primitive);
            }
        } else if (gsonElement.isJsonArray()) {
            List<JsonElement> mappedChildren = new ArrayList<>();
            for (com.google.gson.JsonElement gsonChild : gsonElement.getAsJsonArray()) {
                mappedChildren.add(mapFromGson(gsonChild));
            }
            return JsonArray.of(mappedChildren);
        } else if (gsonElement.isJsonObject()) {
            Map<String, JsonElement> mappedProperties = new HashMap<>();
            for (java.util.Map.Entry<String, com.google.gson.JsonElement> gsonEntry : gsonElement.getAsJsonObject().entrySet()) {
                mappedProperties.put(gsonEntry.getKey(), mapFromGson(gsonEntry.getValue()));
            }
            return JsonObject.of(mappedProperties);
        } else {
            throw new IllegalArgumentException("unknown element type: " + gsonElement);
        }
    }

    static com.google.gson.JsonElement mapToGson(JsonElement jsonElement) {
        Parameters.notNull(jsonElement, "jsonElement");

        if (jsonElement instanceof JsonNull) {
            return com.google.gson.JsonNull.INSTANCE;
        } else if (jsonElement instanceof JsonBoolean b) {
            return new com.google.gson.JsonPrimitive(b.getValue());
        } else if (jsonElement instanceof JsonNumber n) {
            return new com.google.gson.JsonPrimitive(n.getValue());
        } else if (jsonElement instanceof JsonString s) {
            return new com.google.gson.JsonPrimitive(s.getValue());
        } else if (jsonElement instanceof JsonArray a) {
            com.google.gson.JsonArray result = new com.google.gson.JsonArray();
            for (JsonElement child : a.getAsList()) {
                result.add(mapToGson(child));
            }
            return result;
        } else if (jsonElement instanceof JsonObject o) {
            com.google.gson.JsonObject result = new com.google.gson.JsonObject();
            for (java.util.Map.Entry<String, JsonElement> entry : o.getAsMap().entrySet()) {
                result.add(entry.getKey(), mapToGson(entry.getValue()));
            }
            return result;
        } else {
            throw new IllegalArgumentException("unknown element type: " + jsonElement);
        }
    }

}
