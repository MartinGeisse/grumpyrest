/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest;

/**
 * If a handler returns an objet implementing this interface, then it will be unwrapped (possibly multiple times if
 * the wrapped value implements this interface again) before an appropriate responder is created for it.
 */
public interface ResponseValueWrapper {

    Object getWrappedResponseValue();

}
