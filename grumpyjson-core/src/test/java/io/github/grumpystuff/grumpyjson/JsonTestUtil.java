/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializationException;
import io.github.grumpystuff.grumpyjson.deserialize.JsonDeserializer;
import io.github.grumpystuff.grumpyjson.json_model.*;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializationException;
import io.github.grumpystuff.grumpyjson.serialize.JsonSerializer;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

    public static void forBooleans(ConsumerWithException<? super JsonBoolean> consumer) throws Exception {
        consumer.accept(JsonBoolean.of(false));
        consumer.accept(JsonBoolean.of(true));
    }

    // ----------------------------------------------------------------------------------------------------------------
    // numbers
    // ----------------------------------------------------------------------------------------------------------------

    public static void forNumbers(ConsumerWithException<? super JsonNumber> consumer) throws Exception {
        consumer.accept(JsonNumber.of(0));
        consumer.accept(JsonNumber.of(1));
        consumer.accept(JsonNumber.of(2));
        consumer.accept(JsonNumber.of(123));
        consumer.accept(JsonNumber.of(-55));
    }

    // ----------------------------------------------------------------------------------------------------------------
    // strings
    // ----------------------------------------------------------------------------------------------------------------

    public static void forStrings(ConsumerWithException<? super JsonString> consumer) throws Exception {
        consumer.accept(JsonString.of(""));
        consumer.accept(JsonString.of("foo"));
        consumer.accept(JsonString.of("{}"));
        consumer.accept(JsonString.of("0"));
        consumer.accept(JsonString.of("1"));
        consumer.accept(JsonString.of("true"));
        consumer.accept(JsonString.of("false"));
        consumer.accept(JsonString.of("null"));
    }

    // ----------------------------------------------------------------------------------------------------------------
    // arrays
    // ----------------------------------------------------------------------------------------------------------------

    public static final JsonArray EMPTY_ARRAY = JsonArray.of();
    public static final JsonArray SINGLE_INT_ARRAY = buildIntArray(12);
    public static final JsonArray INT_ARRAY = buildIntArray(12, 34, 56);
    public static final JsonArray SINGLE_STRING_ARRAY = buildStringArray("foo");
    public static final JsonArray STRING_ARRAY = buildStringArray("foo", "bar", "baz");

    public static JsonArray buildIntArray(int... numbers) {
        List<JsonElement> elements = new ArrayList<>();
        for (int number : numbers) {
            elements.add(JsonNumber.of(number));
        }
        return JsonArray.of(elements);
    }

    public static JsonArray buildStringArray(String... strings) {
        List<JsonElement> elements = new ArrayList<>();
        for (String string : strings) {
            elements.add(JsonString.of(string));
        }
        return JsonArray.of(elements);
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

    public static final JsonObject EMPTY_OBJECT = JsonObject.of();
    public static final JsonObject OBJECT_WITH_SINGLE_KEY_AND_INT_VALUE = JsonObject.of("foo", JsonNumber.of(123));
    public static final JsonObject OBJECT_WITH_SINGLE_KEY_AND_STRING_VALUE = JsonObject.of("foo", JsonString.of("xyz"));
    public static final JsonObject OBJECT_WITH_SINGLE_KEY_AND_NULL_VALUE = JsonObject.of("foo", JsonNull.INSTANCE);
    public static final JsonObject OBJECT_WITH_SINGLE_KEY_AND_FALSE_VALUE = JsonObject.of("foo", JsonBoolean.of(false));
    public static final JsonObject OBJECT_WITH_SINGLE_KEY_AND_TRUE_VALUE = JsonObject.of("foo", JsonBoolean.of(true));
    public static final JsonObject OBJECT_WITH_SINGLE_INT_KEY_AND_INT_VALUE = JsonObject.of("0", JsonNumber.of(123));
    public static final JsonObject OBJECT_WITH_SINGLE_EMPTY_KEY_AND_INT_VALUE = JsonObject.of("", JsonNumber.of(123));
    public static final JsonObject OBJECT_WITH_TWO_PROPERTIES =
            JsonObject.of("foo", JsonNumber.of(123), "bar", JsonString.of("xyz"));

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
