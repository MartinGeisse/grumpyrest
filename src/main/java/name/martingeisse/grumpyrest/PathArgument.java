/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest;

import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyrest.stringparser.FromStringParserException;

import java.lang.reflect.Type;

public final class PathArgument {

    private final RequestCycle requestCycle;
    private final String name;
    private final String text;

    public PathArgument(RequestCycle requestCycle, String name, String text) {
        this.requestCycle = requestCycle;
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public <T> T getValue(Class<T> clazz) throws FromStringParserException {
        return clazz.cast(getValue((Type)clazz));
    }

    public <T> T getValue(TypeToken<T> typeToken) throws FromStringParserException {
        //noinspection unchecked
        return (T)getValue(typeToken.getType());
    }

    public Object getValue(Type type) throws FromStringParserException {
        return requestCycle.getApi().getFromStringParserRegistry().getParser(type).parseFromString(text, type);
    }

}
