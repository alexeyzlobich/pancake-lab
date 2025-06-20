package org.pancakelab.model.order.exception;

public class OrderCancelledException extends OrderStateException {

    public OrderCancelledException(String message) {
        super(message);
    }

    public OrderCancelledException(String message, Throwable cause) {
        super(message, cause);
    }
}
