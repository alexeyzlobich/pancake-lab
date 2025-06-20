package org.pancakelab.model.order.exception;

public class OrderDeliveredException extends OrderStateException {

    public OrderDeliveredException(String message) {
        super(message);
    }

    public OrderDeliveredException(String message, Throwable cause) {
        super(message, cause);
    }
}
