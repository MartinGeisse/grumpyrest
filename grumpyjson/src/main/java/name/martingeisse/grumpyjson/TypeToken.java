package name.martingeisse.grumpyjson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * This class is an easier way to represent a (parameterized) type than {@link Type}. It solves the
 * following problem: To request deserialization of a type like <code>List&lt;String&gt;</code> from the JSON engine,
 * that type must be specified. A class object cannot include type arguments, so won't do, and a {@link Type}
 * would work but is cumbersome to get. This class solves the problem like so:
 * <p>
 * <code>new TypeToken&lt;List&lt;String&gt;&gt;() {}</code>
 * <p>
 * The type to represent is given as a type argument to this class. It is important to create an anonymous subclass
 * -- hence the curly braces -- because that carries the type information from compile-time to run-time.
 * <p>
 * This class does not support any other features than getting the {@link Type}, such as equals/hashCode or type
 * analysis.
 *
 * @param <T> the type to represent
 */
@SuppressWarnings("unused")
public abstract class TypeToken<T> {

    /**
     * Obtains the type represented by this type token.
     *
     * @return the type
     */
    public final Type getType() {
        if (getClass().getGenericSuperclass() instanceof ParameterizedType parameterizedType) {
            var arguments = parameterizedType.getActualTypeArguments();
            if (parameterizedType.getRawType() == TypeToken.class && arguments.length == 1) {
                return arguments[0];
            }
        }
        throw new RuntimeException("wrong TypeToken usage");
    }

}
