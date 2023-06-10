/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.finish;

import name.martingeisse.grumpyrest.ResponseValueWrapper;

public class FinishRequestException extends RuntimeException implements ResponseValueWrapper {

    private final Object responseValue;

    public FinishRequestException(Object responseValue) {
        this.responseValue = responseValue;
    }

    @Override
    public Object getWrappedResponseValue() {
        return responseValue;
    }

}
