package name.martingeisse.grumpyjson;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

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

        assertEquals(String.class, unrollVariables(ImmutableMap.of(), String.class));

        {
            TypeToken<List<String>> token = new TypeToken<>() {};
            Type type = token.getType();
            assertEquals(type, unrollVariables(ImmutableMap.of(), type));
        }

        {
            Type unboundListType = TypeUtils.parameterize(List.class, typeVariableA); // List<A>
            Type StringListType = TypeUtils.parameterize(List.class, String.class); // List<String>
            assertEquals(StringListType, unrollVariables(ImmutableMap.of(typeVariableA, String.class), unboundListType));
        }

    }

}
