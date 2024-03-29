/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.serialize.JsonSerializationException;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public class JsonTestUtil {

    public interface ConsumerWithException<T> {
        void accept(T t) throws Exception;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // null (for completeness -- makes test code a bit more readable even though it does not reduce code)
    // ----------------------------------------------------------------------------------------------------------------

    public static void forNull(ConsumerWithException<? super JsonNull> consumer) throws Exception {
        consumer.accept(JsonNull.INSTANCE);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // booleans
    // ----------------------------------------------------------------------------------------------------------------

    public static void forBooleans(ConsumerWithException<? super JsonPrimitive> consumer) throws Exception {
        consumer.accept(new JsonPrimitive(false));
        consumer.accept(new JsonPrimitive(true));
    }

    // ----------------------------------------------------------------------------------------------------------------
    // numbers
    // ----------------------------------------------------------------------------------------------------------------

    public static void forNumbers(ConsumerWithException<? super JsonPrimitive> consumer) throws Exception {
        consumer.accept(new JsonPrimitive(0));
        consumer.accept(new JsonPrimitive(1));
        consumer.accept(new JsonPrimitive(2));
        consumer.accept(new JsonPrimitive(123));
        consumer.accept(new JsonPrimitive(-55));
    }

    // ----------------------------------------------------------------------------------------------------------------
    // strings
    // ----------------------------------------------------------------------------------------------------------------

    public static void forStrings(ConsumerWithException<? super JsonPrimitive> consumer) throws Exception {
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

    public static JsonArray buildArray(JsonElement... elements) {
        JsonArray array = new JsonArray();
        for (JsonElement element : elements) {
            array.add(element);
        }
        return array;
    }

    public static void forArrays(ConsumerWithException<? super JsonElement> consumer) throws Exception {
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

    public static void forObjects(ConsumerWithException<? super JsonObject> consumer) throws Exception {
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

    public static void forPrimitive(ConsumerWithException<JsonElement> consumer) throws Exception {
        forNull(consumer);
        forBooleans(consumer);
        forNumbers(consumer);
        forStrings(consumer);
    }

    public static void forNonPrimitive(ConsumerWithException<JsonElement> consumer) throws Exception {
        forArrays(consumer);
        forObjects(consumer);
    }

    public static void forJsonElements(ConsumerWithException<JsonElement> consumer) throws Exception {
        forPrimitive(consumer);
        forNonPrimitive(consumer);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // assertion helpers
    // ----------------------------------------------------------------------------------------------------------------

    @CanIgnoreReturnValue
    public static JsonDeserializationException assertFailsDeserialization(JsonDeserializer deserializer, JsonElement json, TypeToken<?> typeToken) {
        return assertFailsDeserialization(deserializer, json, typeToken.getType());
    }

    @CanIgnoreReturnValue
    public static JsonDeserializationException assertFailsDeserialization(JsonDeserializer deserializer, JsonElement json, Type type) {
        return Assertions.assertThrows(JsonDeserializationException.class, () -> deserializer.deserialize(json, type));
    }

    @CanIgnoreReturnValue
    public static JsonSerializationException assertFailsSerialization(JsonSerializer<?> serializer, Object value) {
        //noinspection unchecked,rawtypes
        return Assertions.assertThrows(JsonSerializationException.class, () -> ((JsonSerializer)serializer).serialize(value));
    }

    @CanIgnoreReturnValue
    public static NullPointerException assertFailsSerializationWithNpe(JsonSerializer<?> serializer, Object value) {
        //noinspection unchecked,rawtypes
        return Assertions.assertThrows(NullPointerException.class, () -> ((JsonSerializer)serializer).serialize(value));
    }

    public static void assertFieldErrors(
            JsonSerializationException exception,
            FieldErrorNode.FlattenedError... expectedFlattenedErrors
    ) {
        assertFieldErrors(exception.getFieldErrorNode(), expectedFlattenedErrors);
    }

    public static void assertFieldErrors(
            JsonDeserializationException exception,
            FieldErrorNode.FlattenedError... expectedFlattenedErrors
    ) {
        assertFieldErrors(exception.getFieldErrorNode(), expectedFlattenedErrors);
    }

    public static void assertFieldErrors(
            FieldErrorNode fieldErrorNode,
            FieldErrorNode.FlattenedError... expectedFlattenedErrors
    ) {
        assertFieldErrors(fieldErrorNode.flatten(), expectedFlattenedErrors);
    }

    public static void assertFieldErrors(
            List<FieldErrorNode.FlattenedError> actualFlattenedErrors,
            FieldErrorNode.FlattenedError... expectedFlattenedErrors
    ) {
        Set<FieldErrorNode.FlattenedError> actualSet = Set.copyOf(actualFlattenedErrors);
        Set<FieldErrorNode.FlattenedError> expectedSet = Set.of(expectedFlattenedErrors);
        Assertions.assertEquals(expectedSet, actualSet);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // other
    // ----------------------------------------------------------------------------------------------------------------

    public static JsonRegistries createRegistries(Object... converters) {
        JsonRegistries registries = JsonRegistries.createDefault();
        for (Object converter : converters) {
            if (converter instanceof JsonSerializer<?> serializer) {
                registries.registerSerializer(serializer);
            }
            if (converter instanceof JsonDeserializer deserializer) {
                registries.registerDeserializer(deserializer);
            }
        }
        return registries;
    }

}
