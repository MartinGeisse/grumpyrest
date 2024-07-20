/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request;

import name.martingeisse.grumpyjson.TypeToken;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParserException;
import name.martingeisse.grumpyrest.request.stringparser.ParseFromStringService;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A concrete argument that was bound to a path parameter. This class contains the argument in its text form, not yet
 * converted to a high-level / application type, and offers methods to perform this conversion. Instances of this
 * class only exist for path parameters, not for fixed (literal) segments of a mounted route.
 */
public final class PathArgument {

    private final String name;
    private final String text;
    private final ParseFromStringService parseFromStringService;

    /**
     * NOT PUBLIC API
     *
     * @param name                   ...
     * @param text                   ...
     * @param parseFromStringService ...
     */
    public PathArgument(String name, String text, ParseFromStringService parseFromStringService) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(parseFromStringService, "parseFromStringService");

        this.name = name;
        this.text = text;
        this.parseFromStringService = parseFromStringService;
    }

    /**
     * Getter for the name of the path parameter.
     * @return the name of the path parameter
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the textual value of the path argument
     * @return the path argument text
     */
    public String getText() {
        return text;
    }

    /**
     * Converts the path argument to the specified class type.
     *
     * @param clazz the class to convert to
     * @return the converted value
     * @param <T> the static type of the class
     * @throws FromStringParserException if conversion fails because the path argument does not conform to the expected
     * format according to the type to convert to
     */
    public <T> T getValue(Class<T> clazz) throws FromStringParserException {
        Objects.requireNonNull(clazz, "clazz");

        return clazz.cast(getValue((Type)clazz));
    }

    /**
     * Converts the path argument to the specified type.
     *
     * @param typeToken a type token for the type to convert to
     * @return the converted value
     * @param <T> the static type to convert to
     * @throws FromStringParserException if conversion fails because the path argument does not conform to the expected
     * format according to the type to convert to
     */
    public <T> T getValue(TypeToken<T> typeToken) throws FromStringParserException {
        Objects.requireNonNull(typeToken, "typeToken");

        //noinspection unchecked
        return (T)getValue(typeToken.getType());
    }

    /**
     * Converts the path argument to the specified type.
     *
     * @param type the type to convert to
     * @return the converted value
     * @throws FromStringParserException if conversion fails because the path argument does not conform to the expected
     * format according to the type to convert to
     */
    public Object getValue(Type type) throws FromStringParserException {
        Objects.requireNonNull(type, "type");

        return parseFromStringService.parseFromString(text, type);
    }

}
