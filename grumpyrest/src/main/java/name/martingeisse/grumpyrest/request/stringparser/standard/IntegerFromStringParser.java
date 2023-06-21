/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.stringparser.standard;

import name.martingeisse.grumpyrest.request.stringparser.FromStringParser;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParserException;

import java.lang.reflect.Type;

public final class IntegerFromStringParser implements FromStringParser {

    @Override
    public boolean supportsType(Type type) {
        return type.equals(Integer.TYPE) || type.equals(Integer.class);
    }

    @Override
    public Object parseFromString(String s, Type type) throws FromStringParserException {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new FromStringParserException("expected integer");
        }
    }

}
