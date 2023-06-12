/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest;

import name.martingeisse.grumpyrest.path.Path;

public record Route(Path path, Handler handler) {

    public Object handle(RequestCycle requestCycle) throws Exception {
        return handler.handle(requestCycle);
    }

}
