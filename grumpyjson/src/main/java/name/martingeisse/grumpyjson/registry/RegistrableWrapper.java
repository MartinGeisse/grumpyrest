/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.registry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

final class RegistrableWrapper<K, V extends Registrable<K, V>> {

    private final K keyRestriction;
    private final V registrable;
    private final AtomicBoolean initializationStarted = new AtomicBoolean(false);
    private final CountDownLatch initializationFinished = new CountDownLatch(1);

    RegistrableWrapper(K keyRestriction, V registrable) {
        this.keyRestriction = keyRestriction;
        this.registrable = registrable;
    }

    boolean supports(K key) {
        return keyRestriction != null ? keyRestriction.equals(key) : registrable.supports(key);
    }

    V use(Registrable.InitializationContext<K, V> initializationContext) throws InterruptedException {
        if (!initializationStarted.getAndSet(true)) {
            // TODO should the registrable have to provide supports(key) while being initialized?
            // auto-generation could do a sanity check: call supports(theKeyItWasCreatedFor) and expect true,
            // and do this before calling initialize(). Do I expect any case in which initialize() changes the
            // supported keys? I think not!
            registrable.initialize(initializationContext);
            initializationFinished.countDown();
        } else {
            initializationFinished.await();
        }
        return registrable;
    }

}
