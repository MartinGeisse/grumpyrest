/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.querystring;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * A QuerystringParser turns a querystring into a Java Object, typically a record since parsers for them can be
 * auto-generated. The concrete type of the object to parse is used both for parser selection and
 * passed to the parser to allow for maximum flexibility. This type may be a Class&lt;?&gt; or a ParameterizedType.
 * <p>
 * For auto-generated record parsers, each parser is responsible for one distinct raw record class, and can handle all
 * parameterized versions of that record.
 */
public interface QuerystringParser {

    /**
     * Checks if this parser supports the specified type.
     *
     * @param type the type to check
     * @return true if supported, false if not
     */
    boolean supportsType(Type type);

    /**
     * Converts the querystring to an application object.
     *
     * @param querystring the querystring to convert, pre-parsed into key/value pairs by the servlet container
     * @param type the type to parse as
     * @return the parsed object
     * @throws QuerystringParsingException on parsing errors, such as wrongly formatted fields, unknown fields,
     * missing fields or duplicate fields
     */
    Object parse(Map<String, String> querystring, Type type) throws QuerystringParsingException;

}
