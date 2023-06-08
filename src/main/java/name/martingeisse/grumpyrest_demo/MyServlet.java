package name.martingeisse.grumpyrest_demo;

import name.martingeisse.grumpyrest.RestApi;
import name.martingeisse.grumpyrest.servlet.RestServlet;

public class MyServlet extends RestServlet {

    public MyServlet() {
        super(createApi());
    }

    private static RestApi createApi() {
        RestApi api = new RestApi();
        return api;
    }

}
