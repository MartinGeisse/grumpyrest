/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.servlet;

import name.martingeisse.grumpyrest.RestApi;
import name.martingeisse.grumpyrest.RequestCycle;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class RestServlet extends HttpServlet {

    private final RestApi api;

    public RestServlet(RestApi api) {
        this.api = api;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RequestCycle requestCycle = new RequestCycle(api, request, response);
        api.handle(requestCycle);
    }

}
