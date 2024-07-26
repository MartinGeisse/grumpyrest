/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest_demo;

import io.github.grumpystuff.grumpyjson.gson.GsonBasedJsonEngine;
import io.github.grumpystuff.grumpyrest.RestApi;
import io.github.grumpystuff.grumpyrest.request.HttpMethod;
import io.github.grumpystuff.grumpyrest_demo.server.GrumpyrestJettyLauncher;

import java.util.List;

public class ToplevelListMain {

    public static void main(String[] args) throws Exception {
        RestApi api = new RestApi(new GsonBasedJsonEngine());
        api.addRoute(HttpMethod.GET, "/", request -> List.of(1, 2, 3));
        api.seal();

        GrumpyrestJettyLauncher launcher = new GrumpyrestJettyLauncher();
        launcher.launch(api);
    }

}
