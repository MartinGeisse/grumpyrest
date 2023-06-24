/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import name.martingeisse.grumpyrest.RequestCycle;
import name.martingeisse.grumpyrest.RestApi;

/**
 * Java servlet that serves a REST API defined by a {@link RestApi} object.
 */
public class RestServlet extends HttpServlet {

    /**
     * needs javadoc because this class is {@link java.io.Serializable}
     */
    private final RestApi api;

    /**
     * needs javadoc because this class is {@link java.io.Serializable}
     */
    private final RequestPathSourcingStrategy requestPathSourcingStrategy;

    /**
     * Constructor.
     *
     * @param api                         the API definition
     * @param requestPathSourcingStrategy how to determine the request path from the servlet request object
     */
    public RestServlet(RestApi api, RequestPathSourcingStrategy requestPathSourcingStrategy) {
        this.api = api;
        this.requestPathSourcingStrategy = requestPathSourcingStrategy;
    }

    @Override
    protected void doGet(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        api.handle(new RequestCycle(api, servletRequest, servletResponse, requestPathSourcingStrategy));
    }

    @Override
    protected void doPut(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        api.handle(new RequestCycle(api, servletRequest, servletResponse, requestPathSourcingStrategy));
    }

    @Override
    protected void doPost(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        api.handle(new RequestCycle(api, servletRequest, servletResponse, requestPathSourcingStrategy));
    }

    @Override
    protected void doDelete(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        api.handle(new RequestCycle(api, servletRequest, servletResponse, requestPathSourcingStrategy));
    }

}
