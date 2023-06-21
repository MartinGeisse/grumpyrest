/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest;

import name.martingeisse.grumpyrest.request.path.Path;

public record Route(Path path, ComplexHandler handler) {

    public Route(String path, ComplexHandler handler) {
        this(Path.parse(path), handler);
    }

    public Route(Path path, SimpleHandler handler) {
        this(path, (RequestCycle requestCycle) -> handler.handle(requestCycle.getHighlevelRequest()));
    }

    public Route(String path, SimpleHandler handler) {
        this(Path.parse(path), handler);
    }

    public Object handle(RequestCycle requestCycle) throws Exception {
        return handler.handle(requestCycle);
    }

}
