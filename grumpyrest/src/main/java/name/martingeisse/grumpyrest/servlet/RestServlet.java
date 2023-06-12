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

import java.io.IOException;

public class RestServlet extends HttpServlet {

    private final RestApi api;

    public RestServlet(RestApi api) {
        this.api = api;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        api.handle(new RequestCycle(api, request, response));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        api.handle(new RequestCycle(api, request, response));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        api.handle(new RequestCycle(api, request, response));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        api.handle(new RequestCycle(api, request, response));
    }

}
