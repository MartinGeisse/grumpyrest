/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest_demo;

import name.martingeisse.grumpyrest.RestApi;
import name.martingeisse.grumpyrest.request.HttpMethod;
import name.martingeisse.grumpyrest_jetty_launcher.GrumpyrestJettyLauncher;

import java.util.List;

public class ToplevelListMain {

    public static void main(String[] args) throws Exception {
        RestApi api = new RestApi();
        api.addRoute(HttpMethod.GET, "/", request -> List.of(1, 2, 3));
        api.seal();

        GrumpyrestJettyLauncher launcher = new GrumpyrestJettyLauncher();
        launcher.launch(api);
    }

}
