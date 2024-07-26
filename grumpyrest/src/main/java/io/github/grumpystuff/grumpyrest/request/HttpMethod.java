package io.github.grumpystuff.grumpyrest.request;

import java.util.Objects;

/**
 * HTTP methods as an enum. This type only covers the methods that we use in API routes. OPTIONS (used for CORS) is not
 * listed here since the routes don't deal with it directly.
 */
public enum HttpMethod {

    /**
     * HTTP GET method
     */
    GET,

    /**
     * HTTP PUT method
     */
    PUT,

    /**
     * HTTP POST method
     */
    POST,

    /**
     * HTTP DELETE method
     */
    DELETE;

    /**
     * Checks whether this method matches the specified other method.
     *
     * @param method the other method
     * @return true if the methods match, false if not
     */
    public final boolean matches(String method) {
        Objects.requireNonNull(method, "method");

        return name().equals(method.toUpperCase());
    }
}
