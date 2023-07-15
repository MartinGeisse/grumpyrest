/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.querystring;

import name.martingeisse.grumpyjson.registry.Registry;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParserRegistry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A registry for {@link QuerystringParser} objects. Usually all parsers in this registry are auto-generated, so the
 * registry only serves as a cache to avoid duplicate work.
 * <p>
 * This registry keep a reference to the {@link FromStringParserRegistry} to parse individual fields (applies to
 * auto-generated parsers only).
 */
public final class QuerystringParserRegistry extends Registry<Type, QuerystringParser> {

    private final FromStringParserRegistry fromStringParserRegistry;

    /**
     * Constructor. The new instance initially contains no parsers.
     *
     * @param fromStringParserRegistry the from-string parser registry that is used to refer to parsers for the
     *                                 individual fields. Note: Sealing this {@link QuerystringParserRegistry} does not
     *                                 automatically seal the {@link FromStringParserRegistry}.
     */
    public QuerystringParserRegistry(FromStringParserRegistry fromStringParserRegistry) {
        this.fromStringParserRegistry = fromStringParserRegistry;
    }

    @Override
    protected String getErrorMessageForUnknownKey(Type type) {
        return "no querystring parser found and can only auto-generate them for record types, found type: " + type;
    }

    @Override
    protected boolean registrableSupports(QuerystringParser registrable, Type key) {
        return registrable.supportsType(key);
    }

    @Override
    protected QuerystringParser generateRegistrable(Type type) {
        Class<?> rawClass;
        if (type instanceof Class<?> c) {
            rawClass = c;
        } else if (type instanceof ParameterizedType p && p.getRawType() instanceof Class<?> c) {
            rawClass = c;
        } else {
            return null;
        }
        if (!rawClass.isRecord()) {
            return null;
        }
        return new QuerystringToRecordParser(rawClass, fromStringParserRegistry);
    }

}
