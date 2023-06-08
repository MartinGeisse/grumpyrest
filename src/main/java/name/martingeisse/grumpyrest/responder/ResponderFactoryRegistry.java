package name.martingeisse.grumpyrest.responder;

import name.martingeisse.grumpyrest.responder.standard.IdentityResponderFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows to register factories that provide {@link Responder} implementations for other types.
 */
public class ResponderFactoryRegistry {

    private final List<ResponderFactory> factories = new ArrayList<>();

    public ResponderFactoryRegistry() {
        factories.add(new IdentityResponderFactory());
    }

    public void clear() {
        factories.clear();
    }

    public void add(ResponderFactory factory) {
        factories.add(factory);
    }

    public Responder createResponder(Object value) {
        for (ResponderFactory factory : factories) {
            Responder responder = factory.createResponder(value);
            if (responder != null) {
                return responder;
            }
        }
        throw new RuntimeException("no responder factory found for value: " + value);
    }

}
