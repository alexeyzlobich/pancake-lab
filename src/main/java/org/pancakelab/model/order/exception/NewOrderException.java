package org.pancakelab.model.order.exception;

public class NewOrderException extends OrderStateException {

    public NewOrderException(String message) {
        super(message);
    }

    public NewOrderException(String message, Throwable cause) {
        super(message, cause);
    }

}
