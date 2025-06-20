package org.pancakelab.model.order.exception;

import org.pancakelab.model.exception.DomainException;

public class OrderStateException extends DomainException {

    public OrderStateException(String message) {
        super(message);
    }

    public OrderStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
