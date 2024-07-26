package io.github.grumpystuff.grumpyrest.servlet;

import jakarta.servlet.http.HttpServletRequest;
import io.github.grumpystuff.grumpyrest.request.path.PathUtil;

import java.util.Objects;

/**
 * This object defines how to obtain the request path from the servlet request. It is needed because different
 * servlet containers treat the path differently, and it can make a difference whether a servlet or a servlet filter
 * is used.
 * <p>
 * The resulting path will have no leading or trailing slash. The empty path gets returned as an empty string.
 */
public enum RequestPathSourcingStrategy {

    /**
     * The request path is built by concatenating the context path, servlet path and path info.
     */
    STARTING_WITH_CONTEXT_PATH {
        @Override
        public String getPath(HttpServletRequest request) {
            Objects.requireNonNull(request, "request");

            return handleResult(mergeParts(mergeParts(getContextPath(request), getServletPath(request)), getPathInfo(request)));
        }
    },

    /**
     * The request path is built by concatenating the servlet path and path info.
     */
    STARTING_WITH_SERVLET_PATH {
        @Override
        public String getPath(HttpServletRequest request) {
            Objects.requireNonNull(request, "request");

            return handleResult(mergeParts(getServletPath(request), getPathInfo(request)));
        }
    },

    /**
     * The request path only consists of the path info.
     */
    PATH_INFO_ONLY {
        @Override
        public String getPath(HttpServletRequest request) {
            Objects.requireNonNull(request, "request");

            return handleResult(getPathInfo(request));
        }
    };

    /**
     * Determines the request path from a servlet request object.
     *
     * @param request the servlet request object
     * @return the path
     */
    public abstract String getPath(HttpServletRequest request);

    private static String getContextPath(HttpServletRequest request) {
        Objects.requireNonNull(request, "request");

        return preparePart(request.getContextPath());
    }

    private static String getServletPath(HttpServletRequest request) {
        Objects.requireNonNull(request, "request");

        return preparePart(request.getServletPath());
    }

    private static String getPathInfo(HttpServletRequest request) {
        Objects.requireNonNull(request, "request");

        return preparePart(request.getPathInfo());
    }

    private static String preparePart(String part) {
        if (part == null) {
            return null;
        }
        part = PathUtil.trimSlashes(part);
        return part.isEmpty() ? null : part;
    }

    private static String mergeParts(String a, String b) {
        return a == null ? b : b == null ? a : (a + '/' + b);
    }

    private static String handleResult(String result) {
        return result == null ? "" : result;
    }

}
