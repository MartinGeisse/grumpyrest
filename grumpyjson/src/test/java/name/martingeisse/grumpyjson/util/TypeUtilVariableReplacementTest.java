/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.util;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeUtilVariableReplacementTest {

    private record MyRecord<A>(A a) {}
    private static final TypeVariable<?> typeVariableA;
    static {
        try {
            typeVariableA = (TypeVariable<?>)MyRecord.class.getDeclaredField("a").getGenericType();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test() {

        assertEquals(String.class, TypeUtil.replaceTypeVariables(String.class, Map.of()));

        {
            TypeToken<List<String>> token = new TypeToken<>() {};
            Type type = token.getType();
            assertEquals(type, TypeUtil.replaceTypeVariables(type, Map.of()));
        }

        {
            Type unboundListType = TypeUtils.parameterize(List.class, typeVariableA); // List<A>
            Type StringListType = TypeUtils.parameterize(List.class, String.class); // List<String>
            assertEquals(StringListType, TypeUtil.replaceTypeVariables(unboundListType, Map.of("A", String.class)));
        }

    }

}
