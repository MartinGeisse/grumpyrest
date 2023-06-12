/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.path;

import java.util.Objects;

public final class VariablePathSegment extends PathSegment {

    private final String variableName;

    public VariablePathSegment(String variableName) {
        this.variableName = Objects.requireNonNull(variableName);
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public boolean matches(String segment) {
        Objects.requireNonNull(segment);
        return true;
    }

}
