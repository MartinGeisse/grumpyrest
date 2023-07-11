/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.stringparser;

import name.martingeisse.grumpyjson.builtin.helper_types.OptionalField;
import name.martingeisse.grumpyrest.ExceptionMessages;

import java.lang.reflect.Type;

/**
 * Implementations of this interface are used to parse a higher-level type from a string. This includes built-in
 * standard types such as {@link Integer}, enums, dates and custom date formats, wrapper types that give the standard
 * types a domain meaning, all the way to complex types that encode multiple fields. The common basis is that the
 * encoded form is a single string, which appears as a path parameter or querystring parameter.
 * <p>
 * There is one extension to this concept: from-string parsers can parse the same type from an <i>absent</i> string.
 * This is used to implement defaults for optional parameters. The simplest case of this is the {@link OptionalField}
 * type, which can be used like in JSON, and will be {@link OptionalField#isAbsent()} if a querystring argument
 * is missing. (Path arguments cannot be missing because then the path would not match anymore). The calling code
 * can then replace the missing value by a default. Other types can do the same as {@link OptionalField} and implement
 * a standard behavior for missing values, which is useful if the same kind of value appears in several places, with
 * the same default behavior.
 * <p>
 * This parser interface is <i>not</i> used to parse higher-level types from JSON fields because their representation
 * is actually different. In a path parameter or a querystring parameter, the number 123 is typically represented as
 * the string "123". In JSON, on the other hand, it would be represented as a JSON number with the value 123, and we'd
 * specifically like to reject the string "123" as invalid. While this might lead to some code duplication, in practice
 * it's not that much and can be reduced by other means, e.g. both parsers delegating to a common method in the
 * background.
 * <p>
 * Conceptually, the same mechanism could be applied to HTTP headers. This is not really that useful though because
 * HTTP is pretty close to using a custom, incompatible format for each and every header. The set of
 * {@link FromStringParser} used by an application, on the other hand, is intended to be consistent, and defined by
 * that application. So we currently don't use these parsers for HTTP headers. (We might reconsider this. A custom
 * format per header is not per se in contradiction with from-string parsers. We would just have to define a custom
 * type and custom parser for each header. But it's unclear if this is really useful.)
 */
public interface FromStringParser {

    /**
     * Checks if this parser supports the specified type.
     *
     * @param type the type to check
     * @return true if supported, false if not
     */
    boolean supportsType(Type type);

    /**
     * Parses a value from a string.
     *
     * @param s the string to parse
     * @param type the type to parse as
     * @return the parsed value
     * @throws FromStringParserException if the string does not conform to the parser's expectation
     */
    Object parseFromString(String s, Type type) throws FromStringParserException;

    /**
     * Parses a value from an absent string. This can be used to return a default for optional parameters.
     * <p>
     * The standard implementation of this method is that missing values are not tolerated, and throws an exception.
     *
     * @param type the type to parse as
     * @return the parsed value
     * @throws FromStringParserException if absent values are not tolerated (this is the default implementation)
     */
    default Object parseFromAbsentString(Type type) throws FromStringParserException {
        throw new FromStringParserException(ExceptionMessages.MISSING_PARAMETER);
    }

}
