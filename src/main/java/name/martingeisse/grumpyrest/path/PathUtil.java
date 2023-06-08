package name.martingeisse.grumpyrest.path;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public final class PathUtil {

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
        return StringUtils.split(pathText, '/');
    }

}
