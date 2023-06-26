package name.martingeisse.grumpyjson.util;

/**
 * A value that can be stored in a {@link Registry} with key type K.
 *
 * @param <K> the key type
 * @param <V> the registrable subtype of this interface
 */
public interface Registrable<K, V extends Registrable<K, V>> {

    /**
     * Checks whether this object supports the specified key,
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
     * @param registry the registry that contains this registrable. This registry can be used to obtain dependency
     *                 registrables. As noted above, such registrables may or may not have been initialized already.
     */
    default void initialize(Registry<K, V> registry) {}

}
