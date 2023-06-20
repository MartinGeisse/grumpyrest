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

public class RestServlet extends HttpServlet {

    private final RestApi api;
    private final RequestPathSourcingStrategy requestPathSourcingStrategy;

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
