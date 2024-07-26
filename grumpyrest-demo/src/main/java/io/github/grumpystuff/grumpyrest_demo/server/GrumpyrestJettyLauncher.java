/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest_demo.server;

import jakarta.servlet.DispatcherType;
import io.github.grumpystuff.grumpyrest.RestApi;
import io.github.grumpystuff.grumpyrest.servlet.RequestPathSourcingStrategy;
import io.github.grumpystuff.grumpyrest.servlet.RestServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.EnumSet;

public final class GrumpyrestJettyLauncher {

    private int port = 8080;
    private RequestPathSourcingStrategy requestPathSourcingStrategy = RequestPathSourcingStrategy.STARTING_WITH_CONTEXT_PATH;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public RequestPathSourcingStrategy getRequestPathSourcingStrategy() {
        return requestPathSourcingStrategy;
    }

    public void setRequestPathSourcingStrategy(RequestPathSourcingStrategy requestPathSourcingStrategy) {
        this.requestPathSourcingStrategy = requestPathSourcingStrategy;
    }

    public void launch(RestApi api) throws Exception {
        Server server = new Server();

        @SuppressWarnings("resource") ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new RestServlet(api, requestPathSourcingStrategy)), "/");

        FilterHolder corsFilterHolder = new FilterHolder(new CorsFilter());
        context.addFilter(corsFilterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));

        server.setHandler(new HandlerList(context, new DefaultHandler()));
        server.start();
        server.join();
    }

}
