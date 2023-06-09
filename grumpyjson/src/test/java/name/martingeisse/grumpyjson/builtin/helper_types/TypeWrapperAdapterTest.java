/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyjson.JsonRegistry;
import name.martingeisse.grumpyjson.JsonTestUtil;
import name.martingeisse.grumpyjson.builtin.IntegerAdapter;
import name.martingeisse.grumpyjson.builtin.ListAdapter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;

import static name.martingeisse.grumpyjson.JsonTestUtil.createRegistry;
import static org.junit.jupiter.api.Assertions.*;

public class TypeWrapperAdapterTest {

    private static final Type INTEGER_TYPE_WRAPPER_TYPE = new TypeToken<TypeWrapper<Integer>>() {}.getType();

    private final JsonRegistry registry = createRegistry(new IntegerAdapter());
    {
        registry.addTypeAdapter(new ListAdapter(registry));
    }
    private final TypeWrapperAdapter adapter = new TypeWrapperAdapter(registry);

    @Test
    public void testSupportsType() {
        assertTrue(adapter.supportsType(TypeWrapper.class));
        assertTrue(adapter.supportsType(INTEGER_TYPE_WRAPPER_TYPE));
    }

    @Test
    public void testFromJson() {
        assertThrows(UnsupportedOperationException.class, () -> adapter.fromJson(new JsonPrimitive(123), TypeWrapper.class));
    }

    @Test
    public void testToJsonPrimitive() {
        List<Integer> list = List.of(1, 2, 3);
        TypeWrapper<?> wrapper = new TypeWrapper<>(list) {};
        assertEquals(JsonTestUtil.buildIntArray(1, 2, 3), adapter.toJson(wrapper, wrapper.getClass()));
    }

}
