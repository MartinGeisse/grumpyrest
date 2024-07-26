/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.builtin.helper_types;

/**
 * This can be used to implement a property that gets ignored during deserialization, and is absent during
 * serialization. Such a property obviously does not add any information to the data structure, but might be useful to
 * maintain compatibility with a field that was used in previous versions.
 * <p>
 * In JSON, an IgnoredField can only be used in a record (actually any place that allows vanishable fields), not in
 * places such as a list, because there is no way to ignore a field or serialize to an absent field there.
 * <p>
 * A shared instance is provided to reduce memory usage, but creating new instances is fine as well.
 */
public record IgnoredField() {

    /**
     * The shared instance of this class. Can be used to avoid memory usage from creating a new instance. This is not
     * a singleton because it is possible to create other instances.
     */
    public static final IgnoredField INSTANCE = new IgnoredField();

}
