/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.querystring;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * A QuerystringParser turns a querystring into a Java Object, typically a record since parsers for them can be
 * auto-generated. The concrete type of the object to parse is used both for parser selection and
 * passed to the parser to allow for maximum flexibility. This type may be a Class<?> or a ParameterizedType.
 * <p>
 * For auto-generated record parsers, each parser is responsible for one distinct raw record class, and can handle all
 * parameterized versions of that record.
 */
public interface QuerystringParser {

    /**
     * Checks if this adapter supports the specified type.
     */
    boolean supportsType(Type type);

    /**
     * Converts a value from JSON.
     */
    Object parse(Map<String, String> querystring, Type type) throws QuerystringParsingException;

}
