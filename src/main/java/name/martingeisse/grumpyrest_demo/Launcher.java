package name.martingeisse.grumpyrest_demo;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;

public final class Launcher {

    private Launcher() {
    }

    public static void launch() throws Exception {
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
}
