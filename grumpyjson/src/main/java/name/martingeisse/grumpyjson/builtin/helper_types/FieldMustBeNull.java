/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

/**
 * This can be used to implement a property that must be null during deserialziation, and always serializes as null.
 * Such a property obviously does not add any information to the data structure, but might be useful to maintain
 * compatibility with a field that was used in previous versions.
 * <p>
 * This can be wrapped in {@link OptionalField} to allow the field to be absent or null, as well as control whether the
 * field gets serialized as absent or null.
 * <p>
 * A shared instance is provided to reduce memory usage, but creating new instances is fine as well.
 */
public record FieldMustBeNull() {

    /**
     * The shared instance of this class. Can be used to avoid memory usage from creating a new instance. This is not
     * a singleton because it is possible to create other instances.
     */
    public static final FieldMustBeNull INSTANCE = new FieldMustBeNull();

}
