package name.martingeisse.grumpyrest_demo;

import com.google.gson.JsonObject;
import name.martingeisse.grumpyrest.RestApi;
import name.martingeisse.grumpyrest.servlet.RestServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class Main {

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
            super(createApi());
        }

        private static RestApi createApi() {
            RestApi api = new RestApi();
            api.addRoute("/", requestCycle -> new JsonObject());
            api.addRoute("/hello", requestCycle -> new GreetingResponse("Hello world!"));
            return api;
        }

        private record GreetingResponse(String greeting) {}

    }

}
