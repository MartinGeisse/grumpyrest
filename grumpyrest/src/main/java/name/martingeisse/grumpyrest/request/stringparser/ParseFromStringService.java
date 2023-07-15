package name.martingeisse.grumpyrest.request.stringparser;

import java.lang.reflect.Type;

/**
 * This interface exposes only the {@link FromStringParserRegistry#parseFromString(String, Type)} method.
 * <p>
 * This interface gets implemented by the registry, not by individual parsers. Use {@link FromStringParser} for
 * individual parsers.
 */
public interface ParseFromStringService {

    // TODO use this interface in querystring parsing? But would need parseFromAbsentString for that

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
