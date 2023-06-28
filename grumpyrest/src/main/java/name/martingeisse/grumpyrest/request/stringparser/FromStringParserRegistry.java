/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.stringparser;

import name.martingeisse.grumpyjson.JsonRegistry;
import name.martingeisse.grumpyrest.RestApi;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This registry keeps {@link FromStringParser}s used to parse path parameters and querystring parameters.
 */
public class FromStringParserRegistry implements ParseFromStringService {

    // This list is not thread-safe, but adding parsers after starting to serve requests would mess up things anyway.
    private final List<FromStringParser> parserList = new ArrayList<>();
    private final ConcurrentMap<Type, FromStringParser> parserMap = new ConcurrentHashMap<>();

    /**
     * Constructor. This constructor does not add any standard parsers, but the {@link RestApi} calling this
     * constructor does.
     */
    public FromStringParserRegistry() {
    }

    // ----------------------------------------------------------------------------------------------------------------
    // configuration-time methods
    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Removes all parsers from this registry. This is useful because the registry that is used by a newly
     * created {@link RestApi} contains default parsers, and the code using it might not want to use them.
     */
    public void clearParsers() {
        parserList.clear();
    }

    /**
     * Adds a parser to this registry, to be used when parsing path arguments and querystring arguments from requests
     * to a {@link RestApi} that uses this registry.
     *
     * @param parser the parser to add
     */
    public void addParser(FromStringParser parser) {
        parserList.add(Objects.requireNonNull(parser, "parser"));
    }

    // ----------------------------------------------------------------------------------------------------------------
    // run-time methods
    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Checks whether the specified type is supported by any parser in this registry.
     * <p>
     * Unlike other registries such as the {@link JsonRegistry}, no auto-generation is supported for from-string
     * parsers (there is no default format they could use), so this will simply check if any of the manually added
     * parsers supports the specified type.
     *
     * @param type the type to check
     * @return true if supported, false if not
     */
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        if (parserMap.containsKey(type)) {
            return true;
        }
        for (var parser : parserList) {
            if (parser.supportsType(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a parser for the specified type. This method will throw an exception if no parser for that type was
     * registered. If multiple parsers have been registered that can handle the specified type, the one registered
     * earlier will take precedence.
     *
     * @param type the type to return an adapter for
     * @return the type adapter
     */
    public FromStringParser getParser(Type type) throws FromStringParserException {
        Objects.requireNonNull(type, "type");

        // computeIfAbsent() cannot be used, if it behaves as it should, because recursively adding recognized types
        // would cause a ConcurrentModificationException. Note that thread safety is not a concern here because,
        // while two threads might *both* decide to create a missing adapter, we just end up with either one of them
        // and they should be equivalent.
        FromStringParser parser = parserMap.get(type);

        // check if one of the registered parsers supports this type
        if (parser == null) {
            for (FromStringParser parserFromList : parserList) {
                if (parserFromList.supportsType(type)) {
                    parser = parserFromList;
                    parserMap.put(type, parser);
                    break;
                }
            }
        }

        // if this failed, then we don't have an appropriate parser
        if (parser == null) {
            throw new FromStringParserException("no from-string parser found for type: " + type);
        }

        return parser;
    }

    @Override
    public Object parseFromString(String text, Type type) throws FromStringParserException {
        return getParser(type).parseFromString(text, type);
    }

}
