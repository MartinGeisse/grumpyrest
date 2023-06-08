package name.martingeisse.grumpyrest;

import name.martingeisse.grumpyrest.path.Path;

public record Route(Path path, Handler handler) {

    public Object handle(RequestCycle requestCycle) {
        return handler.handle(requestCycle);
    }

}
