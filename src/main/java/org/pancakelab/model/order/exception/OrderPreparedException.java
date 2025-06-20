package org.pancakelab.model.order.exception;

public class OrderPreparedException extends OrderStateException {

    public OrderPreparedException(String message) {
        super(message);
    }

    public OrderPreparedException(String message, Throwable cause) {
        super(message, cause);
    }

}
