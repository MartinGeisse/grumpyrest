/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.querystring;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public final class QuerystringParserProxy implements QuerystringParser {

    private QuerystringParser target;

    public void setTarget(QuerystringParser target) {
        this.target = Objects.requireNonNull(target);
    }

    private QuerystringParser needTarget() {
        if (target == null) {
            throw new IllegalStateException("using a QuerystringParserProxy before its target has been set");
        }
        return target;
    }

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        return needTarget().supportsType(type);
    }

    @Override
    public Object parse(Map<String, String> querystring, Type type) throws QuerystringParsingException {
        Objects.requireNonNull(querystring, "querystring");
        Objects.requireNonNull(type, "type");
        return needTarget().parse(querystring, type);
    }

}
