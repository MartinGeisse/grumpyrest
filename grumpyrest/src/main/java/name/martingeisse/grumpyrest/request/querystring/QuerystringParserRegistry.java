/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.querystring;

import name.martingeisse.grumpyrest.request.stringparser.FromStringParserRegistry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class QuerystringParserRegistry {

    private final FromStringParserRegistry fromStringParserRegistry;

    // This list is not thread-safe, but adding parsers after starting to serve requests would mess up
    // things anyway.
    private final List<QuerystringParser> parserList = new ArrayList<>();
    private final ConcurrentMap<Type, QuerystringParser> parserMap = new ConcurrentHashMap<>();

    public QuerystringParserRegistry(FromStringParserRegistry fromStringParserRegistry) {
        this.fromStringParserRegistry = fromStringParserRegistry;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // configuration-time methods
    // ----------------------------------------------------------------------------------------------------------------

    public void clearParsers() {
        parserList.clear();
    }

    public void addParser(QuerystringParser parser) {
        parserList.add(Objects.requireNonNull(parser, "parser"));
    }

    // ----------------------------------------------------------------------------------------------------------------
    // run-time methods
    // ----------------------------------------------------------------------------------------------------------------

    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        if (supportsParserAutoGeneration(type)) {
            return true;
        }
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

    public boolean supportsParserAutoGeneration(Type type) {
        Objects.requireNonNull(type, "type");
        if (type instanceof Class<?> c) {
            return c.isRecord();
        } else if (type instanceof ParameterizedType p && p.getRawType() instanceof Class<?> raw) {
            return raw.isRecord();
        } else {
            return false;
        }
    }

    public QuerystringParser getParser(Type type) {
        Objects.requireNonNull(type, "type");

        // computeIfAbsent() cannot be used, if it behaves as it should, because recursively adding recognized types
        // would cause a ConcurrentModificationException. Note that thread safety is not a concern here because,
        // while two threads might *both* decide to create a missing parser, we just end up with either one of them
        // and they should be equivalent.
        QuerystringParser parser = parserMap.get(type);

        // check if one of the registered parsers supports this type
        if (parser == null) {
            for (QuerystringParser parserFromList : parserList) {
                if (parserFromList.supportsType(type)) {
                    parser = parserFromList;
                    parserMap.put(type, parser);
                    break;
                }
            }
        }

        // check if we can auto-generate a parser
        if (parser == null && supportsParserAutoGeneration(type)) {

            // Next, install a proxy, so that recursive types don't crash the registry. Note that we don't put the
            // parser/proxy into the parserList because we already put it into the parserMap, and it cannot handle
            // any types other than the exact type it gets generated for.
            var proxy = new QuerystringParserProxy();
            parserMap.put(type, proxy);

            // finally, create the actual parser and set it as the proxy's target
            if (type instanceof Class<?> clazz) {
                parser = new QuerystringToRecordParser(clazz, fromStringParserRegistry);
            } else if (type instanceof ParameterizedType parameterizedType) {
                parser = new QuerystringToRecordParser((Class<?>) parameterizedType.getRawType(), fromStringParserRegistry);
            } else {
                throw new RuntimeException("internal error: erroneously selected a record parser for type " + type);
            }

            proxy.setTarget(parser);
        }

        // if this failed, then we don't have an appropriate parser
        if (parser == null) {
            throw new RuntimeException("no querystring parser found and can only auto-generate them for record types, found type: " + type);
        }

        return parser;
    }

}
