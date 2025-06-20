package org.pancakelab.model.order;

import org.pancakelab.model.order.exception.InvalidAddressException;

public record Address(int building, int room) {

    public Address {
        if (building <= 0) {
            throw new InvalidAddressException("Building number must be greater than 0");
        }
        if (room <= 0) {
            throw new InvalidAddressException("Room number must be greater than 0");
        }
    }
}
