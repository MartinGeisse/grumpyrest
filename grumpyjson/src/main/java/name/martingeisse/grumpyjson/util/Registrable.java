package name.martingeisse.grumpyjson.util;

/**
 * A value that can be stored in a {@link Registry} with key type K.
 *
 * @param <K> the key type
 * @param <V> the registrable subtype of this interface
 */
public interface Registrable<K, V extends Registrable<K, V>> {

    /**
     * Checks whether this object supports the specified key.
     * <p>
     * This method must always return the same result for the same key. It must work even when this registrable has not
     * yet been initialized, as well as when called while it is being initialized. This increases parallelism because
     * callers looking for a different key do not have to wait for initialization of this registrable.
     *
     * @param key the key to check
     * @return true if this object supports the key, false if not
     */
    boolean supports(K key);

    /**
     * Initializes this registrable before use. This method will be called after the registry has been sealed and
     * before the registrable is used. It will be called immediately after sealing if this registrable has been added
     * manually, and will be called at some later point if this registrable gets auto-generated during the run-time
     * phase.
     * <p>
     * This method must not rely on other registrables being initialized because there is no defined order of
     * initialization.
     *
     * @param context a limited view of the registry that contains this registrable. The context can be used to obtain
     *                dependency registrables. As noted above, such registrables may or may not have been initialized
     *                already.
     */
    default void initialize(InitializationContext<K, V> context) {}

    /**
     * This interface gives limited registry access to a registrable while it is being initialized.
     */
    interface InitializationContext<K, V extends Registrable<K, V>> {

        /**
         * Resolves a dependency to another registrable. This method is similar to {@link Registry#get(Object)}, but
         * the returned registrable is not guaranteed to be initialized itself. This allows the registry to initialize
         * a set of registrables with cyclic dependencies.
         *
         * @param key the key of the dependency
         * @return the dependency registrable, possibly uninitialized
         */
        V resolveDependency(K key);

    }

}
