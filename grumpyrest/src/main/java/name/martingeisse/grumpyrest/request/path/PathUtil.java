/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.path;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * NOT PUBLIC API
 */
public final class PathUtil {

    private static final Pattern slashPattern = Pattern.compile("/");

    private PathUtil() {
    }

    /**
     * NOT PUBLIC API
     *
     * @param pathText ...
     * @return ...
     */
    public static String trimSlashes(String pathText) {
        Objects.requireNonNull(pathText);
        while (pathText.startsWith("/")) {
            pathText = pathText.substring(1);
        }
        while (pathText.endsWith("/")) {
            pathText = pathText.substring(0, pathText.length() - 1);
        }
        return pathText;
    }

    /**
     * NOT PUBLIC API
     *
     * @param pathText ...
     * @return ...
     */
    public static String[] splitIntoSegments(String pathText) {
        return slashPattern.split(trimSlashes(Objects.requireNonNull(pathText)));
    }

}
