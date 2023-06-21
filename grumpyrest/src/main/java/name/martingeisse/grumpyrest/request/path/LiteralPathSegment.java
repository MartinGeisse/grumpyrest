/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.path;

import java.util.Objects;

public final class LiteralPathSegment extends PathSegment {

    private final String text;

    public LiteralPathSegment(String text) {
        this.text = Objects.requireNonNull(text);
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean matches(String segment) {
        return text.equals(Objects.requireNonNull(segment));
    }

}
