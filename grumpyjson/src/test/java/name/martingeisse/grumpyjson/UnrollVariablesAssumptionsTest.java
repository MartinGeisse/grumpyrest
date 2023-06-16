/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.reflect.TypeUtils.unrollVariables;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Ensures that Apache commons' unrollVariables() as what I think it is.
 */
public class UnrollVariablesAssumptionsTest {

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

        assertEquals(String.class, unrollVariables(Map.of(), String.class));

        {
            TypeToken<List<String>> token = new TypeToken<>() {};
            Type type = token.getType();
            assertEquals(type, unrollVariables(Map.of(), type));
        }

        {
            Type unboundListType = TypeUtils.parameterize(List.class, typeVariableA); // List<A>
            Type StringListType = TypeUtils.parameterize(List.class, String.class); // List<String>
            assertEquals(StringListType, unrollVariables(Map.of(typeVariableA, String.class), unboundListType));
        }

    }

}
