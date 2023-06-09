package name.martingeisse.grumpyjson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public class JsonTestUtil {

    // ----------------------------------------------------------------------------------------------------------------
    // null (for completeness -- makes test code a bit more readable even though it does not reduce code)
    // ----------------------------------------------------------------------------------------------------------------

    public static void forNull(Consumer<? super JsonNull> consumer) {
        consumer.accept(JsonNull.INSTANCE);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // booleans
    // ----------------------------------------------------------------------------------------------------------------

    public static void forBooleans(Consumer<? super JsonPrimitive> consumer) {
        consumer.accept(new JsonPrimitive(false));
        consumer.accept(new JsonPrimitive(true));
    }

    // ----------------------------------------------------------------------------------------------------------------
    // numbers
    // ----------------------------------------------------------------------------------------------------------------

    public static void forNumbers(Consumer<? super JsonPrimitive> consumer) {
        consumer.accept(new JsonPrimitive(0));
        consumer.accept(new JsonPrimitive(1));
        consumer.accept(new JsonPrimitive(2));
        consumer.accept(new JsonPrimitive(123));
        consumer.accept(new JsonPrimitive(-55));
    }

    // ----------------------------------------------------------------------------------------------------------------
    // strings
    // ----------------------------------------------------------------------------------------------------------------

    public static void forStrings(Consumer<? super JsonPrimitive> consumer) {
        consumer.accept(new JsonPrimitive(""));
        consumer.accept(new JsonPrimitive("foo"));
        consumer.accept(new JsonPrimitive("{}"));
        consumer.accept(new JsonPrimitive("0"));
        consumer.accept(new JsonPrimitive("1"));
        consumer.accept(new JsonPrimitive("true"));
        consumer.accept(new JsonPrimitive("false"));
        consumer.accept(new JsonPrimitive("null"));
    }

    // ----------------------------------------------------------------------------------------------------------------
    // arrays
    // ----------------------------------------------------------------------------------------------------------------

    public static final JsonArray EMPTY_ARRAY = new JsonArray();
    public static final JsonArray SINGLE_INT_ARRAY = buildIntArray(12);
    public static final JsonArray INT_ARRAY = buildIntArray(12, 34, 56);
    public static final JsonArray SINGLE_STRING_ARRAY = buildStringArray("foo");
    public static final JsonArray STRING_ARRAY = buildStringArray("foo", "bar", "baz");

    public static JsonArray buildIntArray(int... numbers) {
        JsonArray array = new JsonArray();
        for (int number : numbers) {
            array.add(number);
        }
        return array;
    }

    public static JsonArray buildStringArray(String... strings) {
        JsonArray array = new JsonArray();
        for (String string : strings) {
            array.add(string);
        }
        return array;
    }

    public static void forArrays(Consumer<? super JsonElement> consumer) {
        consumer.accept(EMPTY_ARRAY);
        consumer.accept(SINGLE_INT_ARRAY);
        consumer.accept(INT_ARRAY);
        consumer.accept(SINGLE_STRING_ARRAY);
        consumer.accept(STRING_ARRAY);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // objects
    // ----------------------------------------------------------------------------------------------------------------

    public static final JsonObject EMPTY_OBJECT = new JsonObject();
    public static final JsonObject OBJECT_WITH_SINGLE_KEY_AND_INT_VALUE = buildCustomObject("foo", new JsonPrimitive(123));
    public static final JsonObject OBJECT_WITH_SINGLE_KEY_AND_STRING_VALUE = buildCustomObject("foo", new JsonPrimitive("xyz"));
    public static final JsonObject OBJECT_WITH_SINGLE_KEY_AND_NULL_VALUE = buildCustomObject("foo", JsonNull.INSTANCE);
    public static final JsonObject OBJECT_WITH_SINGLE_KEY_AND_FALSE_VALUE = buildCustomObject("foo", new JsonPrimitive(false));
    public static final JsonObject OBJECT_WITH_SINGLE_KEY_AND_TRUE_VALUE = buildCustomObject("foo", new JsonPrimitive(true));
    public static final JsonObject OBJECT_WITH_SINGLE_INT_KEY_AND_INT_VALUE = buildCustomObject("0", new JsonPrimitive(123));
    public static final JsonObject OBJECT_WITH_SINGLE_EMPTY_KEY_AND_INT_VALUE = buildCustomObject("", new JsonPrimitive(123));
    public static final JsonObject OBJECT_WITH_TWO_PROPERTIES =
            buildCustomObject("foo", new JsonPrimitive(123), "bar", new JsonPrimitive("xyz"));

    public static JsonObject buildCustomObject(String key, JsonElement value) {
        JsonObject object = new JsonObject();
        object.add(key, value);
        return object;
    }

    public static JsonObject buildCustomObject(String key1, JsonElement value1, String key2, JsonElement value2) {
        JsonObject object = buildCustomObject(key1, value1);
        object.add(key2, value2);
        return object;
    }

    public static void forObjects(Consumer<? super JsonObject> consumer) {
        consumer.accept(EMPTY_OBJECT);
        consumer.accept(OBJECT_WITH_SINGLE_KEY_AND_INT_VALUE);
        consumer.accept(OBJECT_WITH_SINGLE_KEY_AND_STRING_VALUE);
        consumer.accept(OBJECT_WITH_SINGLE_KEY_AND_NULL_VALUE);
        consumer.accept(OBJECT_WITH_SINGLE_KEY_AND_FALSE_VALUE);
        consumer.accept(OBJECT_WITH_SINGLE_KEY_AND_TRUE_VALUE);
        consumer.accept(OBJECT_WITH_SINGLE_INT_KEY_AND_INT_VALUE);
        consumer.accept(OBJECT_WITH_SINGLE_EMPTY_KEY_AND_INT_VALUE);
        consumer.accept(OBJECT_WITH_TWO_PROPERTIES);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // combined
    // ----------------------------------------------------------------------------------------------------------------

    public static void forPrimitive(Consumer<JsonElement> consumer) {
        forNull(consumer);
        forBooleans(consumer);
        forNumbers(consumer);
        forStrings(consumer);
    }

    public static void forNonPrimitive(Consumer<JsonElement> consumer) {
        forArrays(consumer);
        forObjects(consumer);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // assertion helpers
    // ----------------------------------------------------------------------------------------------------------------

    public static void assertFailsValidation(JsonTypeAdapter<?> adapter, JsonElement json, TypeToken<?> typeToken) {
        assertFailsValidation(adapter, json, typeToken.getType());
    }

    public static void assertFailsValidation(JsonTypeAdapter<?> adapter, JsonElement json, Type type) {
        //noinspection rawtypes
        Assertions.assertThrows(JsonValidationException.class, () -> ((JsonTypeAdapter)adapter).fromJson(json, type));
    }

    public static void assertFailsGeneration(JsonTypeAdapter<?> adapter, Object value, TypeToken<?> typeToken) {
        assertFailsGeneration(adapter, value, typeToken.getType());
    }

    public static void assertFailsGeneration(JsonTypeAdapter<?> adapter, Object value, Type type) {
        //noinspection unchecked,rawtypes
        Assertions.assertThrows(JsonGenerationException.class, () -> ((JsonTypeAdapter)adapter).toJson(value, type));
    }

    public static void assertFailsGenerationWithNpe(JsonTypeAdapter<?> adapter, Object value, Type type) {
        //noinspection unchecked,rawtypes
        Assertions.assertThrows(NullPointerException.class, () -> ((JsonTypeAdapter)adapter).toJson(value, type));
    }

    // ----------------------------------------------------------------------------------------------------------------
    // other
    // ----------------------------------------------------------------------------------------------------------------

    public static JsonRegistry createRegistry(JsonTypeAdapter<?>... adapters) {
        JsonRegistry registry = new JsonRegistry();
        for (JsonTypeAdapter<?> adapter : adapters) {
            registry.addTypeAdapter(adapter);
        }
        return registry;
    }

}
