package org.pancakelab.model.order.exception;

import org.pancakelab.model.exception.DomainException;

public class InvalidQuantityException extends DomainException {

    public InvalidQuantityException(String message) {
        super(message);
    }

    public InvalidQuantityException(String message, Throwable cause) {
        super(message, cause);
    }

}
