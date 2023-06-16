/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.path;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

public final class PathUtil {

    private static final Pattern slashPattern = Pattern.compile("/");

    private PathUtil() {
    }

    public static String[] splitIntoSegments(String pathText) {
        Objects.requireNonNull(pathText);
        while (pathText.startsWith("/")) {
            pathText = pathText.substring(1);
        }
        while (pathText.endsWith("/")) {
            pathText = pathText.substring(0, pathText.length() - 1);
        }
        return slashPattern.split(pathText);
    }

}
