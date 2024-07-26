/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import name.martingeisse.grumpyjson.JsonRegistries;
import name.martingeisse.grumpyjson.deserialize.JsonDeserializer;
import name.martingeisse.grumpyjson.json_model.JsonString;
import name.martingeisse.grumpyjson.serialize.JsonSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static name.martingeisse.grumpyjson.JsonTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class EnumConverterTest {

    private enum MyEnum {
        FOO_BAR,
        ABC_DEF
    }

    private enum AnotherEnum {
        FOO_BAR,
        ABC_DEF
    }

    private final JsonSerializer<MyEnum> serializer;
    private final JsonDeserializer deserializer;

    public EnumConverterTest() throws Exception {
        JsonRegistries registries = createRegistries();
        registries.seal();
        serializer = registries.getSerializer(MyEnum.class);
        deserializer = registries.getDeserializer(MyEnum.class);
    }

    @Test
    public void testSupportsType() {
        assertTrue(deserializer.supportsTypeForDeserialization(MyEnum.class));
        assertFalse(deserializer.supportsTypeForDeserialization(AnotherEnum.class));
        assertFalse(deserializer.supportsTypeForDeserialization(Enum.class));
        assertFalse(deserializer.supportsTypeForDeserialization(String.class));

        assertTrue(serializer.supportsClassForSerialization(MyEnum.class));
        assertFalse(serializer.supportsClassForSerialization(AnotherEnum.class));
        assertFalse(serializer.supportsClassForSerialization(Enum.class));
        assertFalse(serializer.supportsClassForSerialization(String.class));
    }

    @Test
    public void testDeserializationHappyCase() throws Exception {
        assertEquals(MyEnum.FOO_BAR, deserializer.deserialize(JsonString.of("FOO_BAR"), MyEnum.class));
        assertEquals(MyEnum.ABC_DEF, deserializer.deserialize(JsonString.of("ABC_DEF"), MyEnum.class));
    }

    @Test
    public void testDeserializationUnknownConstant() {
        assertFailsDeserialization(deserializer, JsonString.of("FOO_BAR "), MyEnum.class);
        assertFailsDeserialization(deserializer, JsonString.of(" FOO_BAR"), MyEnum.class);
        assertFailsDeserialization(deserializer, JsonString.of("foo_bar"), MyEnum.class);
    }

    @Test
    public void testDeserializationWrongType() throws Exception {
        forNonPrimitive(json -> assertFailsDeserialization(deserializer, json, String.class));
        forNull(json -> assertFailsDeserialization(deserializer, json, String.class));
        forBooleans(json -> assertFailsDeserialization(deserializer, json, String.class));
        forNumbers(json -> assertFailsDeserialization(deserializer, json, String.class));
    }

    @Test
    public void testSerializationHappyCase() {
        Assertions.assertEquals(JsonString.of("FOO_BAR"), serializer.serialize(MyEnum.FOO_BAR));
    }

    @Test
    public void testSerializationWithNull() {
        assertFailsSerializationWithNpe(serializer, null);
    }

}
