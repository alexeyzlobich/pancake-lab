package org.pancakelab.model.order.exception;

public class OrderCompletedException extends OrderStateException {

    public OrderCompletedException(String message) {
        super(message);
    }

    public OrderCompletedException(String message, Throwable cause) {
        super(message, cause);
    }
}
