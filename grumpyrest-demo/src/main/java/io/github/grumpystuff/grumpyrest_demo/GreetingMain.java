/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest_demo;

import io.github.grumpystuff.grumpyjson.builtin.helper_types.OptionalField;
import io.github.grumpystuff.grumpyjson.gson.GsonBasedJsonEngine;
import io.github.grumpystuff.grumpyrest.RestApi;
import io.github.grumpystuff.grumpyrest.request.HttpMethod;
import io.github.grumpystuff.grumpyrest_demo.server.GrumpyrestJettyLauncher;

public class GreetingMain {

    record MakeGreetingRequest(String name, OptionalField<String> addendum) {}
    record MakeGreetingResponse(String greeting) {}

    public static void main(String[] args) throws Exception {
        RestApi api = new RestApi(new GsonBasedJsonEngine());
        api.addRoute(HttpMethod.GET, "/", request -> "Hello World!");
        api.addRoute(HttpMethod.POST, "/", request -> {
            MakeGreetingRequest requestBody = request.parseBody(MakeGreetingRequest.class);
            if (requestBody.addendum.isPresent()) {
                return new MakeGreetingResponse("Hello, " + requestBody.name + "! " + requestBody.addendum.getValue());
            } else {
                return new MakeGreetingResponse("Hello, " + requestBody.name + "!");
            }
        });

        api.seal();

        GrumpyrestJettyLauncher launcher = new GrumpyrestJettyLauncher();
        launcher.launch(api);
    }

}
