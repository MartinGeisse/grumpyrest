/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.util;

import java.util.List;
import java.util.Objects;

/**
 * NOT PUBLIC API
 */
public class StringUtil {

    private StringUtil() {
    }

    /**
     * ...
     *
     * @param list ...
     * @param separator ...
     * @return ...
     */
    public static String join(final List<?> list, String separator) {
        Objects.requireNonNull(list, "list");
        Objects.requireNonNull(separator, "separator");

        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Object element : list) {
            if (first) {
                first = false;
            } else {
                builder.append(separator);
            }
            builder.append(element);
        }
        return builder.toString();
    }

}
