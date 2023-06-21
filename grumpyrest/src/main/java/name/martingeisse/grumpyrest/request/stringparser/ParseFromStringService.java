package name.martingeisse.grumpyrest.request.stringparser;

import java.lang.reflect.Type;

/**
 * This interface exposes only the {@link FromStringParserRegistry#parseFromString(String, Type)} method.
 */
public interface ParseFromStringService {

    /**
     * Parses value from a string.
     */
    Object parseFromString(String text, Type type) throws FromStringParserException;

}
