package org.pancakelab.model.order.exception;

import org.pancakelab.model.exception.DomainException;

public class InvalidAddressException extends DomainException {

    public InvalidAddressException(String message) {
        super(message);
    }

    public InvalidAddressException(String message, Throwable cause) {
        super(message, cause);
    }
}
