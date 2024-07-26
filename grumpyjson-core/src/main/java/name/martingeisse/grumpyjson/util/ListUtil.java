/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.util;

import java.util.List;

/**
 * NOT PUBLIC API
 */
public final class ListUtil {

    private ListUtil() {
    }

    /**
     * NOT PUBLIC API
     *
     * @param list ...
     * @param <T>  ...
     */
    public static <T> void reverseInPlace(List<T> list) {
        List<T> copy = List.copyOf(list);
        list.clear();
        for (int i = copy.size() - 1; i >= 0; i--) {
            list.add(copy.get(i));
        }
    }
}
