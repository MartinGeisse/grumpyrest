/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.util;

import java.util.List;

public class StringUtil {

    private StringUtil() {
    }

    public static String join(final List<?> list, String separator) {
        StringBuilder builder = new StringBuilder();
        for (Object element : list) {
            if (builder.length() > 0) {
                builder.append(separator);
            }
            builder.append(element);
        }
        return builder.toString();
    }

}
