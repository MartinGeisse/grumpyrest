/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest_demo;

import name.martingeisse.grumpyjson.builtin.helper_types.OptionalField;
import name.martingeisse.grumpyrest.RestApi;
import name.martingeisse.grumpyrest.request.HttpMethod;
import name.martingeisse.grumpyrest_jetty_launcher.GrumpyrestJettyLauncher;

public class GreetingMain {

    record MakeGreetingRequest(String name, OptionalField<String> addendum) {}
    record MakeGreetingResponse(String greeting) {}

    public static void main(String[] args) throws Exception {
        RestApi api = new RestApi();
        api.addRoute(HttpMethod.GET, "/", request -> "Hello World!");
        api.addRoute(HttpMethod.POST, "/", request -> {
            MakeGreetingRequest requestBody = request.parseBody(MakeGreetingRequest.class);
            if (requestBody.addendum.isPresent()) {
                return new MakeGreetingResponse("Hello, " + requestBody.name + "! " + requestBody.addendum.getValue());
            } else {
                return new MakeGreetingResponse("Hello, " + requestBody.name + "!");
            }
        });

        GrumpyrestJettyLauncher launcher = new GrumpyrestJettyLauncher();
        launcher.launch(api);
    }

}
