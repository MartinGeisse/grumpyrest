/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

/**
 * This can be used to implement a property that must be ***JSON-***null during deserialziation, and always serializes as ***JSON-***null.

 * This ***class*** can be wrapped in {@link OptionalField} to allow the field to be absent or null, ...
 */
public record FieldMustBeNull() {
}
