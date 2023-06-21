/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.stringparser.standard;

import name.martingeisse.grumpyrest.request.stringparser.FromStringParser;

import java.lang.reflect.Type;

public final class StringFromStringParser implements FromStringParser {

    @Override
    public boolean supportsType(Type type) {
        return type.equals(String.class);
    }

    @Override
    public Object parseFromString(String s, Type type) {
        return s;
    }

}
