/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest.request.path;

import java.util.Objects;

/**
 * This segment only matches a request path segment with the exact same text.
 */
public final class LiteralPathSegment extends PathSegment {

    private final String text;

    /**
     * Constructor.
     *
     * @param text the expected text for the request path segment
     */
    public LiteralPathSegment(String text) {
        this.text = Objects.requireNonNull(text);
    }

    /**
     * Getter method for the segment text
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    @Override
    public boolean matches(String segment) {
        return text.equals(Objects.requireNonNull(segment));
    }

    @Override
    public String toString() {
        return text;
    }
}
