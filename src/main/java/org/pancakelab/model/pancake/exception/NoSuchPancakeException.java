package org.pancakelab.model.pancake.exception;

import org.pancakelab.model.exception.DomainException;

public class NoSuchPancakeException extends DomainException {

    public NoSuchPancakeException(String message) {
        super(message);
    }

    public NoSuchPancakeException(String message, Throwable cause) {
        super(message, cause);
    }

}
