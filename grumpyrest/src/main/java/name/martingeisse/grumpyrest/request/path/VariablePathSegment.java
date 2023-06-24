/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.path;

import name.martingeisse.grumpyrest.request.PathArgument;

import java.util.Objects;

/**
 * This segment matches any request path segment. The request handling logic will generate a {@link PathArgument} for
 * each such segment.
 */
public final class VariablePathSegment extends PathSegment {

    private final String variableName;

    /**
     * Constructor.
     *
     * @param variableName the name of the variable. This is currently not used anywhere. It is usually specified by
     *                     providing the whole path pattern as a string-based specification that contains variable
     *                     path segments as :name which contains the variable name.
     */
    public VariablePathSegment(String variableName) {
        this.variableName = Objects.requireNonNull(variableName);
    }

    /**
     * Getter for the variable name
     *
     * @return the variable name (currently not used anywhere)
     */
    public String getVariableName() {
        return variableName;
    }

    @Override
    public boolean matches(String segment) {
        Objects.requireNonNull(segment);
        return true;
    }

}
