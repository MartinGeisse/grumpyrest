/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest_demo.shop;

import name.martingeisse.grumpyrest.RestApi;
import name.martingeisse.grumpyrest_jetty_launcher.GrumpyrestJettyLauncher;

public class Main {

    public static void main(String[] args) throws Exception {
        RestApi api = new ShopSystem().buildApi();
        GrumpyrestJettyLauncher launcher = new GrumpyrestJettyLauncher();
        launcher.launch(api);
    }

}
