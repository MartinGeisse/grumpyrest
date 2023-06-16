/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.querystring;

import name.martingeisse.grumpyrest.stringparser.FromStringParserException;

import java.util.Map;

/**
 * Represents one or more errors from parsing the querystring. This is different from a {@link FromStringParserException}
 * in that the latter only describes a problem with a single field.
 */
public class QuerystringParsingException extends Exception {

    private final Map<String, String> fieldErrors;

    public QuerystringParsingException(Map<String, String> fieldErrors) {
        super("exception during querystring parsing");
        this.fieldErrors = Map.copyOf(fieldErrors);
    }

    public final Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

}
