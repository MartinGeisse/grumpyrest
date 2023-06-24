package name.martingeisse.grumpyrest.request.stringparser;

import java.lang.reflect.Type;

/**
 * This interface exposes only the {@link FromStringParserRegistry#parseFromString(String, Type)} method.
 */
public interface ParseFromStringService {

    /**
     * Parses a string to obtain a value of the specified type.
     *
     * @param text the text to parse
     * @param type the type to convert to
     * @return the converted value
     * @throws FromStringParserException if conversion fails because the text does not conform to the expected
     * format according to the type to convert to
     */
    Object parseFromString(String text, Type type) throws FromStringParserException;

}
