/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.registry;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for everything whose lifecycle is divided into configuration phase and run-time phase. Subclasses
 * define what operations are allowed in either phase. The transition from configuration phase to run-time phase is
 * called "sealing" this object. The separation into two phases may allow internal optimization depending on the
 * subclass, and in general enforces to some degree that application code uses this class as intended.
 */
public abstract class Sealable {

    private final AtomicBoolean sealedFlag = new AtomicBoolean(false);

    /**
     * Constructor
     */
    public Sealable() {
    }

    /**
     * Throws an {@link IllegalStateException} if this object has been sealed already.
     */
    protected final void ensureConfigurationPhase() {
        if (sealedFlag.get()) {
            throw new IllegalStateException("this " + getClass().getSimpleName() + " has been sealed already");
        }
    }

    /**
     * Throws an {@link IllegalStateException} if this object has not yet been sealed.
     */
    protected final void ensureRunTimePhase() {
        if (!sealedFlag.get()) {
            throw new IllegalStateException("this " + getClass().getSimpleName() + " has not yet been sealed");
        }
    }

    /**
     * Seals this object, moving from the configuration phase to the run-time phase.
     */
    public final void seal() {
        ensureConfigurationPhase();
        sealedFlag.set(true);
        onSeal();
    }

    /**
     * Subclasses may perform seal-time operations here, e.g. optimize their internal data structures to prepare
     * for run-time.
     */
    protected void onSeal() {
        // does nothing by default
    }

}
