package org.pancakelab.model.order;

public record Address(int building, int room) {

    public Address {
        if (building <= 0) {
            throw new IllegalArgumentException("Building number must be greater than 0");
        }
        if (room <= 0) {
            throw new IllegalArgumentException("Room number must be greater than 0");
        }
    }
}
