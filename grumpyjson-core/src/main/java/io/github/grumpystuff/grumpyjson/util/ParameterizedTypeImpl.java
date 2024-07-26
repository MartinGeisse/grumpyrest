/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * NOT PUBLIC API
 *
 * @param owner     ...
 * @param raw       ...
 * @param arguments ...
 */
public record ParameterizedTypeImpl(
    Type owner,
    Type raw,
    Type... arguments
) implements ParameterizedType {

    /**
     * NOT PUBLIC API
     *
     * @param owner     ...
     * @param raw       ...
     * @param arguments ...
     */
    public ParameterizedTypeImpl {
        arguments = arguments == null ? new Type[0] : arguments.clone();
    }

    @Override
    public Type[] getActualTypeArguments() {
        return arguments;
    }

    @Override
    public Type getRawType() {
        return raw;
    }

    @Override
    public Type getOwnerType() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParameterizedTypeImpl that = (ParameterizedTypeImpl) o;
        return Objects.equals(owner, that.owner) && Objects.equals(raw, that.raw) && Arrays.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(owner, raw);
        result = 31 * result + Arrays.hashCode(arguments);
        return result;
    }

}
