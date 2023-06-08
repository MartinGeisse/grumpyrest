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
