/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.registry;

import io.github.grumpystuff.grumpyjson.util.ListUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Base class for a registry which acts somewhat like a {@link Map}, but with the following differences:
 * <ul>
 *     <li>For each registrable ("value" in Map terminology), the registry can determine the keys which that registrable
 *     supports, i.e. the keys that can be used to obtain that registrable from the registry. Registrables can be added
 *     without specifying a key, and a registrable will be available using any of the keys it supports.</li>
 *     <li>When the registrable for a specific key is requested, the registered registrables are checked last-to-first,
 *     so the last-registered registrable that supports the key will be returned. This allows to register generic
 *     (multi-key) registrables first, and override them for specific keys later. In particular, if the keys are
 *     {@link Type}s, then the registrable for all <code>List&lt;T&gt;</code> types can be added early, and overridden
 *     for specific types like <code>List&lt;String&gt;</code> later.</li>
 *     <li>Depending on the subclass, the registry may support auto-generation of a registrable for a key for which no
 *     registrable was registered manually.</li>
 *     <li>The only allowed ways to manipulate the registry are adding registrables manually, auto-generation, and
 *     clearing the registry. In particular, removing individual registrables is not supported.</li>
 *     <li>The only possible way to query the registry is to ask whether a key is supported, and to get the registrable
 *     for a key. In particular, iteration is not supported.</li>
 *     <li>The registry operates in two phases, configuration phase and run-time phase. In the configuration phase, only
 *     registering objects and clearing the registry are allowed. Querying the registry is not allowed. In the
 *     run-time phase, only querying is allowed, no manipulation. The transition from configuration phase to
 *     run-time phase is called "sealing" the registry. The separation into two phases allows internal optimization.
 *     </li>
 * </ul>
 * <p>
 * The method to determine the supported keys for a registrable is located in the registry, not in the registrable,
 * because in the case of JSON converters, the two methods to get supported keys for the serialization and
 * deserialization case would collide if they were both located in the registrable.
 *
 * @param <K> the key type
 * @param <V> the type of registrable stored in this registry
 */
public abstract class Registry<K, V> extends Sealable {

    private final List<V> manuallyAddedRegistrables = new ArrayList<>();
    private final ConcurrentMap<K, V> map = new ConcurrentHashMap<>();

    /**
     * Constructor.
     */
    public Registry() {
    }

    // ----------------------------------------------------------------------------------------------------------------
    // configuration-time methods
    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Removes all registered objects from this registry. This is useful because the registries used by grumpyjson
     * contain default registrables, and the code using it might not want to use them.
     */
    public final void clear() {
        ensureConfigurationPhase();
        manuallyAddedRegistrables.clear();
    }

    /**
     * Registers a registrable with this registry.
     *
     * @param registrable the registrable to add
     */
    public final void register(V registrable) {
        Objects.requireNonNull(registrable, "registrable");

        ensureConfigurationPhase();
        manuallyAddedRegistrables.add(registrable);
    }

    @Override
    protected void onSeal() {
        ListUtil.reverseInPlace(manuallyAddedRegistrables);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // run-time methods
    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Checks whether the specified key is supported by any registrable that is registered with this registry or can be
     * auto-generated on the fly.
     * <p>
     * Note that there are some reasons why a registered object may claim to support a key, but fail at run-time when it
     * actually encounters that key. Such a key will still be "supported" by that registrable from the point-of-view of
     * the registry. Refer to the documentation of the individual registrables for details.
     *
     * @param key the key to check
     * @return true if supported, false if not
     */
    public final boolean supports(K key) {
        Objects.requireNonNull(key, "key");

        ensureRunTimePhase();
        return getOrNull(key) != null;
    }

    /**
     * Returns a registered object for the specified key, auto-generating it if necessary and possible. This method will
     * throw an exception if no registrable was registered manually that supports the key and no registrable can be
     * auto-generated. If multiple registrables have been registered that can handle the specified key, the one
     * registered later will take precedence.
     *
     * @param key the key to return a registrable for
     * @return the registered object, possibly auto-generated
     * @throws NotRegisteredException if the key is not known to this registry
     */
    public final V get(K key) throws NotRegisteredException {
        V result = getOrNull(key);
        if (result == null) {
            throw new NotRegisteredException(getErrorMessageForUnknownKey(key));
        }
        return result;
    }

    private V getOrNull(K key) {
        Objects.requireNonNull(key, "key");

        ensureRunTimePhase();
        return map.computeIfAbsent(key, ignored -> {
            for (V registrable : manuallyAddedRegistrables) {
                if (registrableSupports(registrable, key)) {
                    return registrable;
                }
            }
            return generateRegistrable(key);
        });
    }

    /**
     * Checks whether the specified registrable supports the specified key.
     * <p>
     * This method must always return the same result for the same registrable and key.
     *
     * @param registrable the registrable which is checked for the key
     * @param key the key to check
     * @return true if the registrable supports the key, false if not
     */
    protected abstract boolean registrableSupports(V registrable, K key);

    /**
     * Auto-generates a registrable in a subclass-specific way. This method does not have to check the key for null,
     * nor whether this registry is in the run-time phase.
     * <p>
     * The returned registrable will only be used for the specified key, not for other keys that it might support as
     * well. To re-use a single auto-generated registrable that supports multiple keys for all the keys it supports,
     * the auto-generation itself must return the same registrable for all those keys.
     * <p>
     * This method must not cause calls to the registry while in progress. In particular, generating a registrable
     * must not query the registry for other registrables to use as dependencies. There are two reasons for this:
     * First, doing so may corrupt the state of this registry or cause deadlock -- the registry is not built to handle
     * that case, simply because it is not needed by any known registrable. Second, doing so may easily cause infinite
     * recursion when requesting the registrable that is already being built.
     * <p>
     * It <i>is</i>, however, allowed to build a registrable that refers to this registry when being used. Such a
     * registrable can take the registry as a constructor parameter and store it internally -- and not access the
     * registry inside its constructor to comply with the above rule -- and then access the registry at run-time. This
     * is how various registrables act in practice. This turned out to be a more practical solution anyway because
     * these registrables do not have all the required information available in the constructor, but only at run-time.
     *
     * @param key the key (never null)
     * @return the auto-generated registrable, or null if auto-generation is not supported for that key
     */
    protected abstract V generateRegistrable(K key);

    /**
     * Produces an error message that gets used in an exception for an unknown key. The error message is one of the
     * most frequent points of contact between the registry and application code, so it should be as developer-friendly
     * as possible.
     *
     * @param key the unknown key
     * @return the error message
     */
    protected abstract String getErrorMessageForUnknownKey(K key);

}
