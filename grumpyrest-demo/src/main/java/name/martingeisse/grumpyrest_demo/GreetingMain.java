/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest_demo;

import name.martingeisse.grumpyjson.builtin.helper_types.OptionalField;
import name.martingeisse.grumpyrest.RestApi;
import name.martingeisse.grumpyrest.servlet.RestServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class GreetingMain {

    public static void main(String[] args) throws Exception {
        Server server = new Server();

        @SuppressWarnings("resource") ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.setConnectors(new Connector[]{connector});

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(MyServlet.class, "/");

        server.setHandler(new HandlerList(context, new DefaultHandler()));
        server.start();
        server.join();
    }

    public static class MyServlet extends RestServlet {

        public MyServlet() {
            super(buildApi());
        }

        record MakeGreetingRequest(String name, OptionalField<String> addendum) {}
        record MakeGreetingResponse(String greeting) {}

        private static RestApi buildApi() {
            RestApi api = new RestApi();
            api.addRoute("/", request -> "Hello World!");
            api.addRoute("/make-greeting", request -> {
                MakeGreetingRequest requestBody = request.parseBody(MakeGreetingRequest.class);
                if (requestBody.addendum.isPresent()) {
                    return new MakeGreetingResponse("Hello, " + requestBody.name + "! " + requestBody.addendum.getValue());
                } else {
                    return new MakeGreetingResponse("Hello, " + requestBody.name + "!");
                }
            });
            return api;
        }

    }

}
