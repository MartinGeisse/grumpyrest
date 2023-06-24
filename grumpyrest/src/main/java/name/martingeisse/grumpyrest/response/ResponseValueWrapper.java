/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.response;

/**
 * If a handler returns an object implementing this interface, then it will be unwrapped (possibly multiple times if
 * the wrapped value implements this interface again) before an appropriate {@link Response} is created for it.
 */
public interface ResponseValueWrapper {

    /**
     * Getter method for the response value wrapped by this wrapper.
     * <p>
     * If the wrapped value is itself an instance of this interface, then the implementation does not have to unwrap
     * that wrapper too -- the caller of this method is expected to unwrap as often as possible.
     *
     * @return the wrapped response value
     */
    Object getWrappedResponseValue();

}
