/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest_demo.shop;

import io.github.grumpystuff.grumpyrest.RestApi;
import io.github.grumpystuff.grumpyrest_demo.server.GrumpyrestJettyLauncher;

public class Main {

    public static void main(String[] args) throws Exception {
        RestApi api = new ShopSystem().buildApi();
        GrumpyrestJettyLauncher launcher = new GrumpyrestJettyLauncher();
        launcher.launch(api);
    }

}
